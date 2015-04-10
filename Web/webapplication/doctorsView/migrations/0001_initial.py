# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
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
            name='SensorUser',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('sensor_id', models.CharField(max_length=200)),
                ('date_created', models.DateTimeField()),
            ],
            options={
                'db_table': 'sensor_user',
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
            ],
            options={
                'db_table': 'user_allergies',
            },
            bases=(models.Model,),
        ),
        migrations.CreateModel(
            name='Users',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('username', models.TextField()),
                ('password', models.TextField()),
                ('user_id', models.CharField(default=None, max_length=200)),
                ('first_name', models.CharField(max_length=100)),
                ('last_name', models.CharField(max_length=100)),
                ('email', models.EmailField(max_length=100)),
                ('user_password', models.CharField(max_length=100)),
                ('dob', models.DateField()),
                ('address', models.CharField(max_length=200)),
                ('gender', models.CharField(max_length=10)),
                ('marital_status', models.CharField(max_length=20)),
                ('height', models.CharField(max_length=20)),
                ('weight', models.CharField(max_length=20)),
                ('blood_type', models.CharField(max_length=20)),
                ('role_type', models.IntegerField(default=1)),
                ('date_created', models.DateTimeField()),
                ('date_modified', models.DateTimeField()),
                ('phone_number', models.CharField(max_length=20)),
            ],
            options={
            },
            bases=(models.Model,),
        ),
        migrations.CreateModel(
            name='UserVaccination',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('vaccination_desc', models.CharField(max_length=1000)),
                ('date_visited', models.DateTimeField()),
                ('date_created', models.DateTimeField()),
                ('date_modified', models.DateTimeField()),
                ('user_id', models.ForeignKey(to='doctorsView.Users', db_column=b'user_id')),
            ],
            options={
                'db_table': 'user_vaccination',
            },
            bases=(models.Model,),
        ),
        migrations.AddField(
            model_name='userallergies',
            name='user_id',
            field=models.ForeignKey(to='doctorsView.Users', db_column=b'user_id'),
            preserve_default=True,
        ),
        migrations.AddField(
            model_name='sensoruser',
            name='user_id',
            field=models.ForeignKey(to='doctorsView.Users', db_column=b'user_id'),
            preserve_default=True,
        ),
    ]
