from .services import STEPS_MAX, KM_MAX


def build_progress(entry):
    steps = entry.steps
    km = float(entry.kilometers)
    return {
        "steps": steps,
        "kilometers": km,
        "step_progress": min(steps / STEPS_MAX, 1.0),
        "distance_progress": min(km / float(KM_MAX), 1.0),
    }
