# -*- coding: utf-8 -*-

"""Event type definitions."""

from datetime import datetime
from enum import Enum

from openapi_client import CameraEvent


class EventType(Enum):
    """Enum for event types."""

    monitoring_started = 0
    monitoring_ended = 1
    person_entered = 2
    person_left = 3


def create_event(event_type: EventType, timestamp: datetime) -> CameraEvent:
    """Create camera event instance.

    Args:
        event_type: event type
        timestamp: event timestamp

    Returns:
        new camera event
    """
    return CameraEvent(event_type=event_type.value, timestamp=timestamp)
