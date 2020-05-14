# -*- coding: utf-8 -*-

"""Data upload module.

Uploads data to API endpoint.
"""

from datetime import datetime

from data_batcher import Batch
from events import EventType, create_event
from openapi_client import ApiClient, CameraEvent, CamerasApi, Configuration
from openapi_client.rest import ApiException


class ApiConnector(object):
    """Uploads events to API endpoint."""

    def __init__(
        self,
        device_id: int,
        configuration: Configuration,
        timestamp: datetime,
    ):
        """Save path for writing.

        Args:
            device_id: device ID for writing
            configuration: API client configuration
            timestamp: start time
        """
        self._device_id = device_id
        self._api_client = ApiClient(configuration)
        self._api = CamerasApi(self._api_client)

        # Send start event
        event = create_event(EventType.monitoring_started, timestamp)
        self._send_single_event(event)

    def send(self, batch: Batch) -> bool:
        """Send batch of events to the server.

        Args:
            batch: batch of events

        Returns:
            true when all uploads finalized
        """
        return self._send_all_as(
            batch.entering, EventType.person_entered,
        ) and self._send_all_as(batch.leaving, EventType.person_left)

    def close(self, timestamp):
        """Notify endpoint that upload ends.

        Args:
            timestamp: end time
        """
        event = create_event(EventType.monitoring_ended, timestamp)
        self._send_single_event(event)
        self._api_client.close()

    def _send_single_event(self, event: CameraEvent) -> bool:
        """Send single CameraEvent to endpoint.

        Args:
            event: CameraEvent to send

        Raises:
            RuntimeError: on upload failure

        Returns:
            true when upload finalizes
        """
        camera_pk = str(self._device_id)
        try:
            self._api.cameras_events_create(camera_pk, event)
        except ApiException as ex:
            msg = 'Could not upload event: {0}'.format(ex)
            raise RuntimeError(msg)

        return True

    def _send_all_as(self, events, event_type: EventType) -> bool:
        """Send all events as given type.

        Args:
            events: list of timestamps
            event_type: type of events to send

        Returns:
            true when all uploads finalized
        """
        for timestamp in events:
            event = create_event(event_type, timestamp)
            if not self._send_single_event(event):
                return False

        return True
