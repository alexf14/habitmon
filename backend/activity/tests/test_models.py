import pytest
from django.db import IntegrityError

from activity.models import ActivityDaily

pytestmark = pytest.mark.django_db


def test_unique_constraint_per_profile_and_date(profile):
    from datetime import date

    ActivityDaily.objects.create(profile=profile, date=date.today(), steps=0, kilometers=0)
    with pytest.raises(IntegrityError):
        ActivityDaily.objects.create(profile=profile, date=date.today(), steps=100, kilometers=1)


def test_str_representation(profile):
    from datetime import date

    entry = ActivityDaily.objects.create(profile=profile, date=date.today(), steps=500, kilometers=0)
    assert "500 pasos" in str(entry)
