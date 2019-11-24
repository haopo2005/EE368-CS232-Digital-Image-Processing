% use the book information we obtained and connect to the amazon to look
% for the price information
function web_access(keywords)
api = 'https://www.amazon.com/s/ref=nb_sb_noss_2?url=search-alias%3Daps&field-keywords=';
url = [api keywords];
web(url);
