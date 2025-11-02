CC = gcc
CFLAGS = -O2 -Wall -Wextra

webpimagesize: webpimagesize.c
	$(CC) $(CFLAGS) -o bin/webpimagesize webpimagesize.c

clean:
	rm -f bin/webpimagesize

.PHONY: clean