# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='User',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('first_name', models.CharField(max_length=100)),
                ('last_name', models.CharField(max_length=100)),
                ('email', models.EmailField(max_length=100)),
                ('dob', models.DateField()),
                ('address', models.CharField(max_length=200)),
                ('gender', models.CharField(max_length=10)),
                ('marital_status', models.CharField(max_length=20)),
                ('height', models.CharField(max_length=20)),
                ('weight', models.CharField(max_length=20)),
                ('blood_type', models.CharField(max_length=20)),
                ('role_type', models.CharField(max_length=20)),
            ],
            options={
                'db_table': 'user',
            },
            bases=(models.Model,),
        ),
    ]
