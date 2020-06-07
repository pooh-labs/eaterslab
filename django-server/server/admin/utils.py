from django.contrib.auth.models import User, Group

def is_admin(user:User):
    if user.is_superuser:
        return True
    if user.groups.filter(name = 'managing-administrator').exists():
        return True
    return False
