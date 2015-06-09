# replayTweetStream
Given a gzipped file of tweets, replay them as though you were streaming them from Twitter's data stream

Currently, only supports a single GZipped file and replays at the per-second level with gaps between messages as determined by their created_at timestamps. 

The code was recently updated to support configurable senders. Simply implement the Sender interface, and you should be good. I've included an implementation to send to the console and to write to a server socket, and both seem to work.

I've also tested this streaming capability with Spark Streaming over a socket and achieved good results.