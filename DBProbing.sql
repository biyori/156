-- sqlite3 auction.db
-- .mode column

-- View the auction winners (highest bidders) for each item
select id, item_id, price, client_uuid from auction_winners where price = (select max(price) from auction_winners i where i.item_id = auction_winners.item_id);

-- View the total spend for bidder

-- View the total items each bidder purchased (incomplete)
select * from (select id, item_id, price, client_uuid from auction_winners where price = (select max(price) from auction_winners i where i.item_id = auction_winners.item_id));
