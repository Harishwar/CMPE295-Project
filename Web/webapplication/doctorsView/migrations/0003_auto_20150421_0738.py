# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
        ('doctorsView', '0002_auto_20150326_1105'),
    ]

    operations = [
        migrations.AlterField(
            model_name='users',
            name='role_type',
            field=models.IntegerField(default=2),
        ),
    ]
