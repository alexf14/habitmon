from datetime import date, timedelta
from decimal import Decimal

import pytest

from activity import services
from activity.models import ActivityDaily

pytestmark = pytest.mark.django_db


def test_get_daily_activity_creates_entry_if_missing(profile):
    entry = services.get_daily_activity(profile)

    assert entry.steps == 0
    assert entry.kilometers == Decimal("0.00")
    assert ActivityDaily.objects.filter(profile=profile, date=date.today()).count() == 1


def test_get_daily_activity_returns_existing_entry(profile):
    services.get_daily_activity(profile)
    entry = services.get_daily_activity(profile)

    assert ActivityDaily.objects.filter(profile=profile).count() == 1
    assert entry.steps == 0


def test_update_steps_only(profile):
    entry = services.update_daily_activity(profile, steps=3579)

    assert entry.steps == 3579
    assert entry.kilometers == Decimal("0.00")


def test_update_kilometers_only(profile):
    entry = services.update_daily_activity(profile, kilometers=3.57)

    assert entry.steps == 0
    assert entry.kilometers == Decimal("3.57")


def test_update_both_fields(profile):
    entry = services.update_daily_activity(profile, steps=5000, kilometers=4.0)

    assert entry.steps == 5000
    assert entry.kilometers == Decimal("4.0")


def test_increment_steps(profile):
    services.update_daily_activity(profile, steps=100)
    entry = services.increment_steps(profile, count=50)

    assert entry.steps == 150


def test_increment_steps_default_count(profile):
    services.update_daily_activity(profile, steps=999)
    entry = services.increment_steps(profile)

    assert entry.steps == 1000


def test_reset_daily_activity(profile):
    services.update_daily_activity(profile, steps=8000, kilometers=6.4)
    entry = services.reset_daily_activity(profile)

    assert entry.steps == 0
    assert entry.kilometers == Decimal("0.00")


def test_entries_are_per_day(profile):
    yesterday = date.today() - timedelta(days=1)
    services.get_daily_activity(profile, for_date=yesterday)
    services.get_daily_activity(profile)

    assert ActivityDaily.objects.filter(profile=profile).count() == 2
