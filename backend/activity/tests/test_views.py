import json

import pytest

from activity import services

pytestmark = pytest.mark.django_db


def test_update_daily_sets_steps(client, profile):
    response = client.put(
        "/activity/daily",
        data=json.dumps({"steps": 3579}),
        content_type="application/json",
    )

    assert response.status_code == 200
    data = response.json()
    assert data["code"] == 200
    assert data["data"]["steps"] == 3579
    assert data["data"]["step_progress"] == pytest.approx(0.3579)


def test_update_daily_sets_kilometers(client, profile):
    response = client.put(
        "/activity/daily",
        data=json.dumps({"kilometers": 3.57}),
        content_type="application/json",
    )

    assert response.status_code == 200
    assert response.json()["data"]["kilometers"] == pytest.approx(3.57)


def test_update_daily_sets_both(client, profile):
    response = client.put(
        "/activity/daily",
        data=json.dumps({"steps": 5000, "kilometers": 4.0}),
        content_type="application/json",
    )

    data = response.json()["data"]
    assert data["steps"] == 5000
    assert data["step_progress"] == pytest.approx(0.5)
    assert data["distance_progress"] == pytest.approx(0.5)


def test_update_daily_rejects_negative_steps(client, profile):
    response = client.put(
        "/activity/daily",
        data=json.dumps({"steps": -1}),
        content_type="application/json",
    )

    assert response.status_code == 400
    assert "non-negative" in response.json()["message"]


def test_update_daily_rejects_negative_km(client, profile):
    response = client.put(
        "/activity/daily",
        data=json.dumps({"kilometers": -0.1}),
        content_type="application/json",
    )

    assert response.status_code == 400


def test_update_daily_requires_at_least_one_field(client, profile):
    response = client.put(
        "/activity/daily",
        data=json.dumps({}),
        content_type="application/json",
    )

    assert response.status_code == 400
    assert "At least one" in response.json()["message"]


def test_update_daily_rejects_invalid_body(client, profile):
    response = client.put(
        "/activity/daily",
        data="not-json",
        content_type="application/json",
    )

    assert response.status_code == 400


def test_step_progress_caps_at_1(client, profile):
    response = client.put(
        "/activity/daily",
        data=json.dumps({"steps": 15000}),
        content_type="application/json",
    )

    assert response.json()["data"]["step_progress"] == pytest.approx(1.0)


def test_distance_progress_caps_at_1(client, profile):
    response = client.put(
        "/activity/daily",
        data=json.dumps({"kilometers": 10.0}),
        content_type="application/json",
    )

    assert response.json()["data"]["distance_progress"] == pytest.approx(1.0)


def test_increment_steps_endpoint(client, profile):
    services.update_daily_activity(profile, steps=100)
    response = client.post(
        "/activity/daily/steps/increment",
        data=json.dumps({"count": 5}),
        content_type="application/json",
    )

    assert response.status_code == 200
    assert response.json()["data"]["steps"] == 105


def test_increment_steps_default_count(client, profile):
    response = client.post(
        "/activity/daily/steps/increment",
        data=json.dumps({}),
        content_type="application/json",
    )

    assert response.status_code == 200
    assert response.json()["data"]["steps"] == 1
