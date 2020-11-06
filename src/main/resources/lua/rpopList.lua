--返回List
local array = {}
local arrayLength = redis.call("llen", KEYS[1])
if (arrayLength > tonumber(ARGV[1])) then
    arrayLength = tonumber(ARGV[1])
end
for i = 1, arrayLength do
    array[i] = redis.call("rpop", KEYS[1])
end
return array