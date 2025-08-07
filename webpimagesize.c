#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>

int main(int argc, char *argv[]) {
    if (argc != 2) {
        fprintf(stderr, "Usage: %s <webp_file>\n", argv[0]);
        return 1;
    }

    FILE *file = fopen(argv[1], "rb");
    if (!file) {
        fprintf(stderr, "Error: Cannot open file %s\n", argv[1]);
        return 1;
    }

    char header[12];
    if (fread(header, 1, 12, file) != 12) {
        fprintf(stderr, "Error: Cannot read file header\n");
        fclose(file);
        return 1;
    }

    if (memcmp(header, "RIFF", 4) != 0 || memcmp(header + 8, "WEBP", 4) != 0) {
        fprintf(stderr, "Error: Not a valid WebP file\n");
        fclose(file);
        return 1;
    }

    char chunk_header[8];
    uint32_t width = 0, height = 0;

    while (fread(chunk_header, 1, 8, file) == 8) {
        uint32_t chunk_size = chunk_header[4] | (chunk_header[5] << 8) | 
                             (chunk_header[6] << 16) | (chunk_header[7] << 24);

        if (memcmp(chunk_header, "VP8 ", 4) == 0) {
            fseek(file, 6, SEEK_CUR);
            
            uint8_t frame_tag[3];
            if (fread(frame_tag, 1, 3, file) != 3) break;
            
            if ((frame_tag[0] & 1) != 0) {
                fprintf(stderr, "Error: Unsupported WebP format\n");
                fclose(file);
                return 1;
            }

            uint8_t size_data[4];
            if (fread(size_data, 1, 4, file) != 4) break;
            
            width = size_data[0] | (size_data[1] << 8);
            height = size_data[2] | (size_data[3] << 8);
            break;
            
        } else if (memcmp(chunk_header, "VP8L", 4) == 0) {
            uint8_t sig;
            if (fread(&sig, 1, 1, file) != 1) break;
            
            if (sig != 0x2f) {
                fprintf(stderr, "Error: Invalid VP8L signature\n");
                fclose(file);
                return 1;
            }

            uint8_t size_data[5];
            if (fread(size_data, 1, 5, file) != 5) break;
            
            width = (size_data[0] | (size_data[1] << 8) | 
                    ((size_data[2] & 0x3f) << 16)) + 1;
            height = (((size_data[2] & 0xc0) >> 6) | (size_data[3] << 2) |
                     ((size_data[4] & 0x03) << 10)) + 1;
            break;
            
        } else if (memcmp(chunk_header, "VP8X", 4) == 0) {
            fseek(file, 4, SEEK_CUR);
            
            uint8_t size_data[6];
            if (fread(size_data, 1, 6, file) != 6) break;
            
            width = (size_data[0] | (size_data[1] << 8) | (size_data[2] << 16)) + 1;
            height = (size_data[3] | (size_data[4] << 8) | (size_data[5] << 16)) + 1;
            break;
            
        } else {
            fseek(file, chunk_size + (chunk_size & 1), SEEK_CUR);
        }
    }

    fclose(file);

    if (width == 0 || height == 0) {
        fprintf(stderr, "Error: Could not determine image dimensions\n");
        return 1;
    }

    printf("{:width %u :height %u}\n", width, height);
    return 0;
}