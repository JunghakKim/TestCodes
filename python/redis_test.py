import redis

if __name__ == "__main__":
    r = redis.StrictRedis(host="localhost", port=6379)
    result = r.zrank("steps", "junghak")
    print result
