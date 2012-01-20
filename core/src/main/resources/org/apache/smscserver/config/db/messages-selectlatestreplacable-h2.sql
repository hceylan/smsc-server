SELECT * FROM SMSC_MESSAGE 
WHERE sourceaddr = {sourceaddr} AND destaddr = {destaddr} AND servicetype = {servicetype} AND STATUS = 'PENDING'
ORDER BY received DESC LIMIT 1;