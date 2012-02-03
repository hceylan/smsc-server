--<ScriptOptions statementTerminator=";"/>

CREATE TABLE IF NOT EXISTS SMSC_MESSAGE (      
	id VARCHAR(37) NOT NULL PRIMARY KEY,
	datacoding TINYINT,
	defaultmessage TINYINT,
	destaddr VARCHAR(21) NOT NULL,
	destaddrnpi TINYINT,
	destaddrton TINYINT,
	esmclass TINYINT,
	messageLength INTEGER,
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


CREATE INDEX IF NOT EXISTS SRC_ADDR ON SMSC_MESSAGE (sourceaddr);

CREATE INDEX IF NOT EXISTS REPLACE_MSG ON SMSC_MESSAGE (sourceaddr, destaddr, servicetype, status, received DESC);
