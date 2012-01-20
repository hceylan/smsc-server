SELECT * FROM SMSC_MESSAGE 
WHERE sourceaddr = {sourceaddr} AND destaddr = {destaddr} AND servicetype = {servicetype} 
ORDER BY received DESC LIMIT 1;