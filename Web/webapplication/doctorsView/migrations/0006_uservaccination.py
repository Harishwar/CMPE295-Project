# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
        ('doctorsView', '0005_auto_20150225_0734'),
    ]

    operations = [
        migrations.CreateModel(
            name='UserVaccination',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('vaccination_desc', models.CharField(max_length=1000)),
                ('date_visited', models.DateTimeField()),
                ('date_created', models.DateTimeField()),
                ('date_modified', models.DateTimeField()),
                ('user_id', models.ForeignKey(to='doctorsView.User', db_column=b'user_id')),
            ],
            options={
            },
            bases=(models.Model,),
        ),
    ]
