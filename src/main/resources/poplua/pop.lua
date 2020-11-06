local not_empty = function(x)
    return (type(x) == "table") and (not x.err) and (#x ~= 0)
end

local qName = ARGV[1] --队列名称
local currentTime = ARGV[2] --当前时间，这个需要从外部传入，不能使用redis自身时间，如果使用自身时间可能导致redis本身的backup在重放请求时出现不一致性
local considerAsFailMaxTimeSpan = ARGV[3] --超时时间设定，当消息超过一定时间还没有ack则认为此消息需要再次入队

local zsetName = qName .. 'BACKUP'
local hashName = qName .. 'CONTEXT'

local tmp = redis.call('ZRANGEBYSCORE', zsetName, '-INF', tonumber(currentTime) - tonumber(considerAsFailMaxTimeSpan), 'LIMIT', 0, 1)
if (not_empty(tmp)) then
    redis.call('ZREM', zsetName, tmp[1]) --此处拿出的为消息的唯一id
    redis.call('LPUSH', qName, redis.call('HGET', hashName, tmp[1]))
end
tmp = redis.call('RPOP', qName)
if (tmp) then
    local msg = cjson.decode(tmp)
    local id = msg['id']
    redis.call('ZADD', zsetName, tonumber(currentTime), id)
    redis.call('HSET', hashName, id, tmp)
end
return tmp