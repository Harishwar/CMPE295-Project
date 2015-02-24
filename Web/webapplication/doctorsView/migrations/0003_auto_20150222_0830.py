# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
        ('doctorsView', '0002_auto_20150221_1728'),
    ]

    operations = [
        migrations.CreateModel(
            name='SensorUser',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('sensor_id', models.CharField(max_length=200)),
                ('user_id', models.ForeignKey(to='doctorsView.User')),
            ],
            options={
                'db_table': 'sensor_user',
            },
            bases=(models.Model,),
        ),
        migrations.AlterField(
            model_name='user',
            name='role_type',
            field=models.IntegerField(default=1),
            preserve_default=True,
        ),
    ]
