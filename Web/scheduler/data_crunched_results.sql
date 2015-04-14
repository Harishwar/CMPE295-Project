SELECT * FROM SensorResults.crunched_results;
SET SQL_SAFE_UPDATES = 0;

delete from SensorResults.crunched_results where user_id='test1@test1.com'

insert into crunched_results values('test1@test1.com','2015-04-03','93.8','1224')
insert into crunched_results values('test1@test1.com','2015-04-04','83.8','1224')
insert into crunched_results values('test1@test1.com','2015-04-02','99.8','1224')
insert into crunched_results values('test1@test1.com','2015-04-01','90.8','1224')


insert into crunched_results values('test2@test2.com','2015-04-03','95.8','1224')
insert into crunched_results values('test1@test2.com','2015-04-04','102.8','1224')
insert into crunched_results values('test2@test2.com','2015-04-02','96.8','1224')

insert into crunched_results values('test1@test2.com','2015-04-01','92.8','1224')


insert into crunched_results values('test3@test3.com','2015-04-05','83.8','1224')
insert into crunched_results values('test3@test3.com','2015-04-06','103.8','1224')
insert into crunched_results values('test3@test3.com','2015-04-07','91.8','1224')
insert into crunched_results values('test3@test3.com','2015-04-08','94.8','1224')

insert into crunched_results values('test2@test2.com','2015-04-04','95.8','1224')

insert into crunched_results values('test1@test2.com','2015-04-03','78.8','1224')

insert into crunched_results values('test1@test1.com','2015-04-02','92.7','1224')
insert into crunched_results values('test1@test1.com','2015-04-01','93.2','1224')