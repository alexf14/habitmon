import uuid

from django.db import models


class ActivityDaily(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    profile = models.ForeignKey("profiles.Profile", on_delete=models.CASCADE, related_name="daily_activities")
    date = models.DateField()
    steps = models.PositiveIntegerField(default=0)
    kilometers = models.DecimalField(max_digits=7, decimal_places=2, default=0)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    class Meta:
        verbose_name = "actividad diaria"
        verbose_name_plural = "actividades diarias"
        constraints = [
            models.UniqueConstraint(fields=["profile", "date"], name="unique_daily_activity"),
        ]

    def __str__(self):
        return f"{self.profile} — {self.date} ({self.steps} pasos)"
