#include <string.h>

#include "usf.h"
#include "usf_internal.h"

#include "cpu_hle.h"
#include "os.h"
#include "audiolib.h"

// Nintendo 64 Standard (and non standard) library functions, for HLE


int numEntries = 0;


_HLE_Entry entrys[] = {
        {"__osEnqueueThread",      0, 20, {0x8C, 0x98, 0,    0,    0x8C, 0xAF, 0,    4,    0,    0x80, 0xC8, 0x25, 0x8F, 0xE,  0,    4,    1,    0xCF, 8,  0x2A},                                                                                                                                                                                                                                                                                0, 0, __osEnqueueThread},

        {"__osRestoreInt",         0, 28, {0x40, 8,    0x60, 0,    1,    4,    0x40, 0x25, 0x40, 0x88, 0x60, 0,    0,    0,    0,    0,    0,    0,    0,  0,    3,    0xE0, 0,    8,    0,    0,    0,    0},                                                                                                                                                                                                                                   0, 0, __osRestoreInt},
        {"__osDisableInt",         0, 32, {0x40, 8,    0x60, 0,    0x24, 1,    0xFF, 0xFE, 1,    1,    0x48, 0x24, 0x40, 0x89, 0x60, 0,    0x31, 2,    0,  1,    0,    0,    0,    0,    3,    0xE0, 0,    8,    0,    0,    0,    0},                                                                                                                                                                                                           0, 0, __osDisableInt},
//	{"osStartThread",0,32,{0x27,0xBD,0xFF,0xD8,0xAF,0xBF,0,0x1C,0xAF,0xA4,0,0x28,0xAF,0xB1,0,0x18,0xC,-1,-1,-1,0xAF,0xB0,0,0x14,0x8F,0xAE,0,0x28,0x24,1,0,1,0,0x40,0x80,0x25},0,0,osStartThread},
        //{"osPiStartDma",0,52,{0x27,0xBD,0xFF,0xD8,0x3C,0xE,-1,-1,0x8D,0xCE,-1,-1,0xAF,0xBF,0,0x1C,0xAF,0xA4,0,0x28,0xAF,0xA5,0,0x2C,0xAF,0xA6,0,0x30,0xAF,0xA7,0,0x34,0xAF,0xB1,0,0x18,0x15,0xC0,0,3,0xAF,0xB0,0,0x14,0x10,0,0,0x32,0x24,2,0xFF,0xFF},0,0,osPiStartDma}
        //{"osRecvMesg",0,60,{0x27,0xBD,0xFF,0xD8,0xAF,0xBF,0,0x1C,0xAF,0xA4,0,0x28,0xAF,0xA5,0,0x2C,0xAF,0xA6,0,0x30,0xAF,0xB1,0,0x18,0xC,-1,-1,-1,0xAF,0xB0,0,0x14,0x8F,0xAE,0,0x28,0,0x40,0x80,0x25,0x8D,0xCF,0,8,0x15,0xE0,0,0x12,0,0,0,0,0x8f,0xb8,0,0x30,0x17,0,0,5},0,0,osRecvMesg},

        {"saveThreadContext",      0, 32, {0x3C, 5,    -1,   -1,   0x8C, 0xA5, -1,   -1,   0x40, 8,    0x60, 0,    0x8C, 0xBB, 0,    0x18, 0x35, 8,    0,  2,    0xAC, 0xA8, 1,    0x18, 0xFC, 0xB0, 0,    0x98, 0xFC, 0xB1, 0,    0xA0},                                                                                                                                                                                                        0, 0, saveThreadContext},
        {"loadThreadContext",      0, 40, {0x3C, 4,    -1,   -1,   0xC,  -1,   -1,   -1,   0x24, 0x84, -1,   -1,   0x3C, 1,    -1,   -1,   0xAC, 0x22, -1, -1,   0x24, 8,    0,    4,    0xA4, 0x48, 0,    0x10, 0,    0x40, 0xD0, 0x25, 0x3C, 8,    -1,   -1,   0x8F, 0x5B, 1,    0x18},                                                                                                                                                        0, 0, loadThreadContext},


        {"osSetIntMask",           0, 44, {0x40, 0xC,  0x60, 0,    0x31, 0x82, 0xFF, 1,    0x3C, 8,    -1,   -1,   0x25, 8,    -1,   -1,   0x8D, 0xB,  0,  0,    0x24, 1,    0xFF, 0xFF, 1,    0x61, 0x40, 0x26, 0x31, 8,    0xFF, 0,    0,    0x48, 0x10, 0x25, 0x3C, 0xA,  0xA4, 0x30, 0x8D, 0x4A, 0,    0xC},                                                                                                                                 0, 0, osSetIntMask},
        {"osVirtualToPhysical",    0, 36, {0x27, 0xBD, 0xFF, 0xE8, 0xAF, 0xA4, 0,    0x18, 0x8F, 0xAE, 0,    0x18, 0x3C, 1,    0x80, 0,    0xAF, 0xBF, 0,  0x14, 1,    0xC1, 8,    0x2B, 0x14, 0x20, 0,    7,    0x3C, 1,    0xA0, 0,    1,    0xC1, 8,    0x2B},                                                                                                                                                                                0, 0, osVirtualToPhysical},


        {"alCopy",                 0, 32, {0,    0x80, 0x10, 0x25, 0,    0xA0, 0x18, 0x25, 0x18, 0xC0, 0,    0x18, 0,    0,    0x38, 0x25, 0x30, 0xC5, 0,  3,    0x10, 0xA0, 0,    9,    0,    0xA0, 0x20, 0x25, 0x90, 0x4E, 0,    0},                                                                                                                                                                                                           0, 0, alCopy},
        {"alLink",                 0, 28, {0x8C, 0xAE, 0,    0,    0xAC, 0x85, 0,    4,    0xAC, 0x8E, 0,    0,    0x8C, 0xA2, 0,    0,    0x10, 0x40, 0,  2,    0,    0,    0,    0,    0xAC, 0x44, 0,    4},                                                                                                                                                                                                                                   0, 0, alLink},
        {"alUnlink",               0, 28, {0x8C, 0x82, 0,    0,    0x50, 0x40, 0,    4,    0x8C, 0x82, 0,    4,    0x8C, 0x8E, 0,    4,    0xAC, 0x4E, 0,  4,    0x8C, 0x82, 0,    4,    0x10, 0x40, 0,    3},                                                                                                                                                                                                                                   0, 0, alUnLink},

        {"osAiSetNextBuffer",      0, 32, {0x27, 0xBD, 0xFF, 0xE0, 0x3C, 0xF,  -1,   -1,   0x91, 0xEF, -1,   -1,   0xAF, 0xA4, 0,    0x20, 0x8F, 0xAE, 0,  0x20, 0xAF, 0xBF, 0,    0x14, 0xAF, 0xA5, 0,    0x24, 0x11, 0xE0, 0,    3},                                                                                                                                                                                                           0, 0, osAiSetNextBuffer},

        {"alLink (DK64)",          0, 20, {0x8C, 0xAE, 0,    0,    0xAC, 0x8E, 0,    0,    0xAC, 0x85, 0,    4,    0x8C, 0xAF, 0,    0,    0x11, 0xE0, 0,  3},                                                                                                                                                                                                                                                                                   0, 0, alLink},
        {"alUnLink (DK64)",        0, 28, {0x8C, 0x8E, 0,    0,    0x11, 0xC0, 0,    4,    0,    0,    0,    0,    0x8C, 0x8F, 0,    4,    0x8C, 0x98, 0,  0,    0xAF, 0xF,  0,    4,    0x8C, 0x99, 0,    4},                                                                                                                                                                                                                                   0, 0, alUnLink},

        {"alEvtqPostEvent",        0, 64, {0x27, 0xBD, 0xFF, 0xD0, 0xAF, 0xBF, 0,    0x14, 0xAF, 0xA4, 0,    0x30, 0xAF, 0xA5, 0,    0x34, 0xAF, 0xA0, 0,  0x20, 0x24, 4,    0,    1,    0xC,  -1,   -1,   -1,   0xAF, 0xA6, 0,    0x38, 0x8F, 0xAE, 0,    0x30, 0x8F, 0xA7, 0,    0x38, 0,    0x40, 0x28, 0x25, 0x8D, 0xC8, 0,  0,    0x15, 0,    0, 5,    1,    0,    0x20, 0x25, 0xC,  -1, -1, -1, 0,   0x40, 0x20, 0x25},                    0, 0, alEvtqPostEvent},

        {"alEvtqPostEvent (DK64)", 0, 64, {0x27, 0xBD, 0xFF, 0xD0, 0xAF, 0xBF, 0,    0x14, 0xAF, 0xA4, 0,    0x30, 0xAF, 0xA5, 0,    0x34, 0xAF, 0xA6, 0,  0x38, 0xAF, 0xA0, 0,    0x20, 0xC,  -1,   -1,   -1,   0x24, 4,    0,    1,    0xAF, 0xA2, 0,    0x1C, 0x8F, 0xAE, 0,    0x30, 0x8D, 0xCF, 0,    0,    0xAF, 0xAF, 0,  0x2C, 0x8F, 0xB8, 0, 0x2C, 0x17, 0,    0,    5,    0,    0,  0,  0,  0xC, -1,   -1,   -1, 0x8F, 0xA4, 0, 0x1C}, 0, 0, alEvtqPostEvent},
        {"alEvtqPostEvent (CBFD)", 0, 56, {0x27, 0xBD, 0xFF, 0xC0, 0xAF, 0xBF, 0,    0x14, 0xAF, 0xA4, 0,    0x40, 0xAF, 0xA5, 0,    0x44, 0xAF, 0xA6, 0,  0x48, 0xAF, 0xA7, 0,    0x4C, 0xAF, 0xA0, 0,    0x30, 0x8F, 0xAE, 0,    0x4C, 0x31, 0xCF, 0,    2,    0x11, 0xE0, 0,    4,    0,    0,    0,    0,    0xC,  -1,   -1, -1,   0x24, 4,    0, 1,    0xAF, 0xA2, 0,    0x2C},                                                             0, 0, alEvtqPostEvent},
        {"alEvtqPostEvent (BT)",   0, 60, {0x27, 0xBD, 0xFF, 0xD0, 0xAF, 0xBF, 0,    0x14, 0xAF, 0xA4, 0,    0x30, 0xAF, 0xA5, 0,    0x34, 0xAF, 0xA6, 0,  0x38, 0xAF, 0xA7, 0,    0x3C, 0xAF, 0xA0, 0,    0x20, 0xC,  -1,   -1,   -1,   0x24, 4,    0,    1,    0xAF, 0xA2, 0,    0x1C, 0x8F, 0xAE, 0,    0x30, 0x8D, 0xCF, 0,  0,    0xAF, 0xAF, 0, 0x2C, 0x8F, 0xB8, 0,    0x2C, 0x17, 0,  0,  5},                                            0, 0, alEvtqPostEvent},

        {"alAudioFrame",           0, 52, {0x27, 0xBD, 0xFF, 0x48, 0xAF, 0xB1, 0,    0x30, 0x3C, 0x11, -1,   -1,   0x8E, 0x31, 0 -
                                                                                                                               1,    -1,   0xAF, 0xBF, 0,  0x4C, 0xAF, 0xB7, 0,    0x48, 0xAF, 0xB6, 0,    0x44, 0xAF, 0xB5, 0,    0x40, 0xAF, 0xB4, 0,    0x3C, 0xAF, 0xB3, 0,    0x38, 0xAF, 0xB2, 0,    0x34, 0xAF, 0xB0, 0,  0x2C, 0xF7, 0xB6, 0, 0x20},                                                                                     0, 0, alAudioFrame},

        //{"alAudioFrame (DK64)",0,64,{0x27,0xBD,0xFF,0xC0,0xAF,0xBF,0,0x1C,0xAF,0xA4,0,0x40,0xAF,0xA5,0,0x44,0xAF,0xA6,0,0x48,0xAF,0xA7,0,0x4C,0xAF,0xB1,0,0x18,0xAF,0xB0,0,0x14,0xA7,0xA0,0,0x3A,0x8F,0xAE,0,0x40,0xAF,0xAE,0,0x34,0x8F,0xAF,0,0x48,0xAF,0xAF,0,0x28,0x3C,0x18,0x80,0x75,0x8F,0x18,0xC,0x74,0x8F,0x19,0,0},0,0,alAudioFrame},

};
//int 0xA4,0xA6,0,0x38

//char foundlist[2048];

int sort_entrys(void *a, void *b) {
    _HLE_Entry *_a = (_HLE_Entry *) a;
    _HLE_Entry *_b = (_HLE_Entry *) b;
    return _b->used - _a->used;
}

int CPUHLE_Scan(usf_state_t *state) {
    int i = 0, j = 0;
    unsigned char c = 0;
    long d = 0;
    int entrySize = 0;
    int entry = 0;
    void *address = 0;
    int good = 1; //, needcomma = 0;
    _HLE_Entry *entries;
    unsigned entries_used = 0;

    numEntries = sizeof(entrys) / sizeof(_HLE_Entry);

    entries = state->cpu_hle_entries = realloc(state->cpu_hle_entries, sizeof(entrys));
    memcpy(entries, entrys, sizeof(entrys));

    //for(i=0; i < numEntries; i++)
    //	entries[i].used = 0;

    //memset(foundlist,0,2048);

    for (i = 0; i < (state->RdramSize - 64); i += 4) {

        for (entry = 0; entry < numEntries; entry++) {

            if (entries[entry].used)
                continue;

            good = 1;
            entrySize = entries[entry].length;

            for (j = 0; j < entrySize; j++) {
                address = state->N64MEM + i + j;
                //address = i;
                c = *(unsigned char *) (address);
                d = entries[entry].bytes[j ^ 3];

                if ((c != d) && (d != -1)) {
                    good = 0;
                    break;
                }
            }

            if (good == 1 && i < (state->RdramSize - 64)) {
                //char buf[256];
                //if(needcomma) {
                //	sprintf(buf,", %s", entries[entry].name);
                //} else {
                //	sprintf(buf,"%s", entries[entry].name);
                //}

                //needcomma = 1;
                //strcat(foundlist,buf);

                entries[entry].used = 1;
                entries[entry].phys = i;
                ++entries_used;
                break;
            }
        }

    }

    qsort(entries, numEntries, sizeof(*entries), sort_entrys);
    state->cpu_hle_entry_count = entries_used;

    //printf("<--------------HLE Functions Found--------------->\n%s<------------------------------------------------>\n", foundlist);
    //printf("HLE Functions found: %s\n", foundlist);

    return 0;
}

int DoCPUHLE(usf_state_t *state, unsigned long loc) {
    int i = 0;
    uintptr_t real_addr = PageVRAM2(loc);
    _HLE_Entry *entries = state->cpu_hle_entries;
    unsigned numEntries = state->cpu_hle_entry_count;

    for (i = 0; i < numEntries; i++) {

        if (entries[i].phys == real_addr) {
            //printf("CPU HLEing using %d at %08x (phys: %08x) \"%s\"\n", entries[i].name, loc, entries[i].phys, entries[i].name);

            if (entries[i].location(state, entries[i].phys)) {
                //	printf("done\n");
                return 1;
            } else
                return 0;
        }

    }

    return 0;
}
