insert into post(name) values
('Manager'),
('Ressource Humaine'),
('Chauffeur'),
('Technicien');

insert into leave_type(type,max_duration,description) values
('congé payé',30,''),
('congé pour formation',1095,''),
('congé pour éducation dans les domaines social, civique et syndical',12,''),
('congé de maladie',30,''),
('congé de cure thermale',60,''),
('congé de longue durée',180,''),
('congé de maternité',90,''),
('congé de paternité',15,''),
('absence permis',20,''),
('autre absence',30,'');

INSERT INTO worker(first_name, last_name, post_id, birth_date, sex, email, address,cin,entrance_datetime,phone) VALUES 
('Angelica','Jacobi',1,'1958-12-29','F','Angelica.Jacobi251571.9893@hotmail.com','Analakely','11111111111','2022-07-10','0331700000'),
('Dominic','McGlynn',1,'1993-10-12','M','Dominic_McGlynn4210852_739@hotmail.com','Ankadikely','11111111112','2022-01-18','0331701000'),
('Guy','Bartell',2,'2001-12-10','M','Guy.Bartell82942.36129@hotmail.com','Ambohitrarahaba','11111111113','2021-07-24','0331700600'),
('Alvin','Johnston',3,'1999-06-12','M','Alvin.Johnston68911_56@hotmail.com','67Ha','11111111151','2019-04-1','0331705000'),
('Margie','Greenholt',4,'1993-12-31','F','Margie016587.21821@gmail.com','Ambatobe','11111111711','2010-03-12','0331700800');

INSERT INTO leave_taken(comment,start_date,end_date,leave_id,worker_id) VALUES 
('','2022-09-14','2022-09-20',1,1),
('','2021-08-01','2021-08-02',1,2),
('','2022-09-21','2022-09-24',2,1),
('','2020-09-02','2020-09-02',3,3),
('','2020-09-14','2020-09-20',4,4);
