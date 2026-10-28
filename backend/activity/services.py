from datetime import date
from decimal import Decimal

from .models import ActivityDaily

STEPS_MAX = 10000
KM_MAX = Decimal("8.0")


def get_daily_activity(profile, for_date=None):
    if for_date is None:
        for_date = date.today()
    entry, _ = ActivityDaily.objects.get_or_create(
        profile=profile,
        date=for_date,
        defaults={"steps": 0, "kilometers": Decimal("0.00")},
    )
    return entry


def update_daily_activity(profile, steps=None, kilometers=None):
    entry = get_daily_activity(profile)
    if steps is not None:
        entry.steps = steps
    if kilometers is not None:
        entry.kilometers = Decimal(str(kilometers))
    entry.save()
    return entry


def increment_steps(profile, count=1):
    entry = get_daily_activity(profile)
    entry.steps += count
    entry.save()
    return entry


def reset_daily_activity(profile, for_date=None):
    if for_date is None:
        for_date = date.today()
    ActivityDaily.objects.filter(profile=profile, date=for_date).update(
        steps=0, kilometers=Decimal("0.00")
    )
    return get_daily_activity(profile, for_date)
