CC = gcc
CFLAGS = -O2 -Wall -Wextra

webpimagesize: webpimagesize.c
	$(CC) $(CFLAGS) -o webpimagesize webpimagesize.c

clean:
	rm -f webpimagesize

.PHONY: clean