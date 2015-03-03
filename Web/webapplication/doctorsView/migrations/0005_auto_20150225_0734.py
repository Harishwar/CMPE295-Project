# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations
import datetime
from django.utils.timezone import utc


class Migration(migrations.Migration):

    dependencies = [
        ('doctorsView', '0004_auto_20150222_1136'),
    ]

    operations = [
        migrations.CreateModel(
            name='Allergies',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('allergy_name', models.CharField(max_length=100)),
                ('allergy_desc', models.CharField(max_length=300)),
                ('date_created', models.DateTimeField()),
            ],
            options={
                'db_table': 'allergies',
            },
            bases=(models.Model,),
        ),
        migrations.CreateModel(
            name='UserAllergies',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('date_created', models.DateTimeField()),
                ('date_modified', models.DateTimeField()),
                ('allergy_id', models.ForeignKey(to='doctorsView.Allergies', db_column=b'allergy_id')),
                ('user_id', models.ForeignKey(to='doctorsView.User', db_column=b'user_id')),
            ],
            options={
                'db_table': 'user_allergies',
            },
            bases=(models.Model,),
        ),
        migrations.AddField(
            model_name='sensoruser',
            name='date_created',
            field=models.DateTimeField(default=datetime.datetime(2015, 2, 25, 15, 34, 15, 839000, tzinfo=utc)),
            preserve_default=False,
        ),
        migrations.AddField(
            model_name='user',
            name='user_id',
            field=models.CharField(default=None, max_length=200),
            preserve_default=True,
        ),
    ]
