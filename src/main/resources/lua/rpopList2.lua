--返回List，以及剩余队列长度
local array = {}
local arrayLength = redis.call("llen", KEYS[1])
if (arrayLength > tonumber(ARGV[1])) then
    arrayLength = tonumber(ARGV[1])
end
for i = 1, arrayLength do
    array[i] = redis.call("rpop", KEYS[1])
end
--剩余队列大小
array[table.getn(array) + 1] = redis.call("llen", KEYS[1])
return array