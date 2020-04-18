from rest_framework import viewsets
from django.views.decorators.csrf import csrf_exempt

from .serializers import CafeteriaSerializer

from .models import Cafeteria


# TODO: handle different roles for API views

class CafeteriaViewSet(viewsets.ModelViewSet):
    queryset = Cafeteria.objects.all().order_by('id')
    serializer_class = CafeteriaSerializer


from rest_framework import status
from rest_framework.decorators import api_view
from rest_framework.response import Response
from .models import CameraEvent
from .serializers import CameraEventSerializer


# this code is left for testing
@csrf_exempt
@api_view(['POST'])
def events_batch(request):
    print(request.get_full_path())
    print(request.data)
    print("yo")
    if request.method == 'POST':
        print(request)
        serializer = CameraEventSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


@csrf_exempt
@api_view(['POST'])
def event(request, camera_id):
    if request.method == 'POST':
        request.data['camera_id'] = camera_id  # insert camera id from the url
        serializer = CameraEventSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
