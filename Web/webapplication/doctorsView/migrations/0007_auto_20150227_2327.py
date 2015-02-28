# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
        ('doctorsView', '0006_uservaccination'),
    ]

    operations = [
        migrations.AlterModelTable(
            name='uservaccination',
            table='user_vaccination',
        ),
    ]
