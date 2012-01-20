-- Licensed to the Apache Software Foundation (ASF) under one
-- or more contributor license agreements.  See the NOTICE file
-- distributed with this work for additional information
-- regarding copyright ownership.  The ASF licenses this file
-- to you under the Apache License, Version 2.0 (the
-- "License"); you may not use this file except in compliance
-- with the License.  You may obtain a copy of the License at
-- 
--  http://www.apache.org/licenses/LICENSE-2.0
-- 
-- Unless required by applicable law or agreed to in writing,
-- software distributed under the License is distributed on an
-- "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
-- KIND, either express or implied.  See the License for the
-- specific language governing permissions and limitations
-- under the License.

CREATE MEMORY TABLE IF NOT EXISTS SMSC_MESSAGE (      
	id VARCHAR(37) NOT NULL PRIMARY KEY,
	datacoding TINYINT,
	defaultmessage TINYINT,
	destaddr VARCHAR(21) NOT NULL,
	destaddrnpi TINYINT,
	destaddrton TINYINT,
	esmclass TINYINT,
	messageLength TINYINT,
	nexttrydelivertime TIMESTAMP,
	priorityflag TINYINT,
	protocolid TINYINT,
	received TIMESTAMP,
	replacedby VARCHAR(37),
	replaced VARCHAR(37),
	scheduledate TIMESTAMP,
	servicetype VARCHAR(6) NOT NULL,
	shortmessage VARCHAR(255),
	sourceaddr VARCHAR(21) NOT NULL,
	sourceaddrnpi TINYINT,
	sourceaddrton TINYINT,
	status VARCHAR(10),
	validityperiod TINYINT
);
