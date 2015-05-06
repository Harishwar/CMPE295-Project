# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
        ('doctorsView', '0004_auto_20150426_0427'),
    ]

    operations = [
        migrations.AlterField(
            model_name='users',
            name='address',
            field=models.CharField(default=None, max_length=200),
        ),
        migrations.AlterField(
            model_name='users',
            name='gender',
            field=models.CharField(default=None, max_length=10),
        ),
        migrations.AlterField(
            model_name='users',
            name='height',
            field=models.CharField(default=1, max_length=20),
        ),
        migrations.AlterField(
            model_name='users',
            name='role_type',
            field=models.IntegerField(default=2),
        ),
        migrations.AlterField(
            model_name='users',
            name='user_id',
            field=models.CharField(default=None, max_length=200),
        ),
        migrations.AlterField(
            model_name='users',
            name='weight',
            field=models.CharField(default=1, max_length=20),
        ),
    ]
