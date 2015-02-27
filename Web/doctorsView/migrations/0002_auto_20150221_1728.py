# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations
import datetime
from django.utils.timezone import utc


class Migration(migrations.Migration):

    dependencies = [
        ('doctorsView', '0001_initial'),
    ]

    operations = [
        migrations.AddField(
            model_name='user',
            name='date_created',
            field=models.DateTimeField(default=datetime.datetime(2015, 2, 22, 1, 27, 58, 765000, tzinfo=utc)),
            preserve_default=False,
        ),
        migrations.AddField(
            model_name='user',
            name='date_modified',
            field=models.DateTimeField(default=datetime.datetime(2015, 2, 22, 1, 28, 9, 452000, tzinfo=utc)),
            preserve_default=False,
        ),
        migrations.AddField(
            model_name='user',
            name='phone_number',
            field=models.CharField(default=1234567890, max_length=20),
            preserve_default=False,
        ),
    ]
