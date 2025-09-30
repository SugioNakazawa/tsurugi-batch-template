INSERT INTO daily_sales (item_id, sales_year, sales_month, sales_day, sales_qty, sales_amount) 
SELECT item_id, sales_year, sales_month, sales_day, SUM(sales_qty), SUM(sales_amount)
FROM sales_detail
GROUP BY item_id, sales_year, sales_month, sales_day;