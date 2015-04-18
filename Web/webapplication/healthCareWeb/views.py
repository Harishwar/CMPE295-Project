from django.shortcuts import render

def index(request):
    return render(request,"index.html");

def password(request):
    return render(request,"password.html");
