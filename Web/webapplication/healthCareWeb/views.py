from django.shortcuts import render
from django.shortcuts import redirect

def index(request):
    if request.session.get('user_id') and request.session.get('role_id')==1:
        return redirect('doctorsView/dashboard')
    elif request.session.get('user_id') and request.session.get('role_id')==2:
        return redirect('usersView/viewDashBoard')
    else:
        return render(request,"index.html");

def password(request):
    return render(request,"password.html");
