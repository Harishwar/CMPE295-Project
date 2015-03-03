# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
        ('doctorsView', '0003_auto_20150222_0830'),
    ]

    operations = [
        migrations.AlterField(
            model_name='sensoruser',
            name='user_id',
            field=models.ForeignKey(to='doctorsView.User', db_column=b'user_id'),
            preserve_default=True,
        ),
    ]
