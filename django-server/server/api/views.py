from rest_framework import viewsets

from .serializers import CafeteriaSerializer

from .models import Cafeteria


# TODO: handle different roles for API views

class CafeteriaViewSet(viewsets.ModelViewSet):
    queryset = Cafeteria.objects.all().order_by('id')
    serializer_class = CafeteriaSerializer
