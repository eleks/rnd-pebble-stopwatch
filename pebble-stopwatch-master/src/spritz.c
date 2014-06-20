/*
 * Pebble Stopwatch - spritz window
 * Copyright (C) 2014 Nick Savula
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


#include <pebble.h>
#include <pebble_fonts.h>

#include "laps.h"
#include "common.h"

static Window* window;

#define MAX_LAPS 30
#define SPRITZ_SIZE 14
#define PIVOT_CHAR 7

#define BUTTON_LAP BUTTON_ID_DOWN
#define BUTTON_SPRITZ BUTTON_ID_SELECT
#define BUTTON_START BUTTON_ID_UP

static TextLayer* spritzLayers[SPRITZ_SIZE];

static GFont system_font;
static GFont common_font;

//static AppSync app_sync;
//static uint8_t app_sync_buffer[124];

static char *  str;
static char ** res  = NULL;
static char *  p;
static int n_spaces = 0, i;
static int wpm = 0;
static bool stopSpritz;

void config_prov(Window *window);
void toggle_spritz_handler(ClickRecognizerRef recognizer, Window *window);
void reset_stopwatch_handler(ClickRecognizerRef recognizer, Window *window);

int find_pivot(char *string);

static void error_callback(DictionaryResult dict_error, AppMessageResult app_message_error, void *context) {
    APP_LOG(APP_LOG_LEVEL_DEBUG, "App Message Sync Error: %d", app_message_error);
}

static void tuple_changed_callback(const uint32_t key, const Tuple* new_tuple, const Tuple* old_tuple, void* context) {
    switch (key) {
        case 0:
            // Text to Spritz
            memcpy(str, new_tuple->value->cstring, new_tuple->length);
            
            /* split string and append tokens to 'res' */
            p = strtok (str, " ");
            
            while (p) {
                res = realloc (res, sizeof (char*) * ++n_spaces);
                
//                if (res == NULL)
//                    exit (-1); /* memory allocation failed */
                
                res[n_spaces-1] = p;
                
                p = strtok (NULL, " ");
            }
            
//            text_layer_set_text(location_layer, new_tuple->value->cstring);
            break;
        case 1:
            // WPM
            wpm = new_tuple->value->uint32;
            break;
    }
}

void init_spritz_window() {
	window = window_create();
    window_set_background_color(window, GColorWhite);
    
//    Tuplet initial[] = {
//        TupletCString(0, "No Location"),
//    };
//    app_sync_init(&app_sync, app_sync_buffer, sizeof(app_sync_buffer), initial, ARRAY_LENGTH(initial),
//                  tuple_changed_callback, error_callback, NULL);

    common_font = fonts_load_custom_font(resource_get_handle(RESOURCE_ID_FONT_DEJAVU_SANS_SUBSET_18));
    system_font =  fonts_get_system_font(FONT_KEY_GOTHIC_18_BOLD);

    Layer *root_layer = window_get_root_layer(window);
    
    // Arrange for user input.
    window_set_click_config_provider(window, (ClickConfigProvider) config_prov);

    // Set up the spritz layers. These will be made visible later.
    for(int i = 0; i < SPRITZ_SIZE; ++i)
    {
		spritzLayers[i] = text_layer_create(GRect(2 + i * 10, 40, 10, 10));
        text_layer_set_background_color(spritzLayers[i], GColorClear);
        text_layer_set_font(spritzLayers[i], common_font);
        
        if (i == PIVOT_CHAR)
        {
            text_layer_set_font(spritzLayers[i], system_font);
        }
        
        text_layer_set_text_color(spritzLayers[i], GColorBlack);
//        text_layer_set_text(lap_layers[i], lap_times[i]);
        layer_add_child(root_layer, (Layer*)spritzLayers[i]);
    }
    
//    // Add a prompt for more laps.
//	spritzLayer = text_layer_create(GRect(0, 61, 144, 30));
//    text_layer_set_background_color(spritzLayer, GColorClear);
////    text_layer_set_font(spritzLayer, laps_font);
//    text_layer_set_text_color(spritzLayer, GColorBlack);
//    text_layer_set_text_alignment(spritzLayer, GTextAlignmentCenter);
//    text_layer_set_text(spritzLayer, "Spritz!");
//    layer_add_child(root_layer, (Layer*)spritzLayer);
}

void deinit_spritz_window() {
	for(int i = 0; i < SPRITZ_SIZE; ++i) {
		text_layer_destroy(spritzLayers[i]);
	}

	fonts_unload_custom_font(common_font);
    fonts_unload_custom_font(system_font);
	window_destroy(window);
}

void show_spritz() {
    window_stack_push(window, true);
}

int find_pivot(char *string)
{
    if (strlen(string) == 0)
    {
        return 0;
    }
    
    int pivot = (strlen(string) + 2) / 4;
    
    if (pivot > 4)
    {
        pivot = 4;
    }
    
    if (string[pivot] == ' ')
    {
        -- pivot;
    }
    
    return pivot;
}

void toggle_spritz_handler(ClickRecognizerRef recognizer, Window *window) {
    // start spritzing
    int wait_ms = 60 / wpm * 1000;
    
    for (i = 0; i < (n_spaces+1); ++i)
    {
//        text_layer_set_text(spritzLayer, res[i]);
//        GSize size = text_layer_get_content_size(spritzLayer);
        
        int offset = 7 - find_pivot(res[i]);
        
        for(int i = 0; i < SPRITZ_SIZE; ++i)
        {
            if (i >= offset && i < (int)strlen(res[i]) + offset)
            {
                text_layer_set_text(spritzLayers[i], res[i]);
            }
            else
            {
                text_layer_set_text(spritzLayers[i], " ");
            }
        }
        
        if (stopSpritz)
        {
            stopSpritz = false;
            break;
        }
        
        psleep(wait_ms);
    }
}

void reset_spritz_handler(ClickRecognizerRef recognizer, Window *window) {
    stopSpritz = true;
}

void config_prov(Window *window) {
	window_single_click_subscribe(BUTTON_SPRITZ, (ClickHandler)toggle_spritz_handler);
	window_single_click_subscribe(BUTTON_START, (ClickHandler)reset_spritz_handler);
}
