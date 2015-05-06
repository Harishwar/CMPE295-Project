# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
        ('doctorsView', '0003_auto_20150421_0738'),
    ]

    operations = [
        migrations.AlterField(
            model_name='users',
            name='role_type',
            field=models.IntegerField(default=1),
        ),
        migrations.AlterField(
            model_name='users',
            name='user_id',
            field=models.CharField(max_length=200),
        ),
    ]
