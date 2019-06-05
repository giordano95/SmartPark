#include "mbed.h"
#include <stdio.h>
#include "SpwfSAInterface.h"
#include "SPWFSAxx.h"
#include "TLSSocket.h"

#include "mbed_trace.h"
#include "https_request.h"
#include "ultrasonic.h"

WiFiInterface *wifi;

bool is_free = true;
int distances[10];
static const char slot_free[] = "{\"slot02\":true}";
static const char slot_full[] = "{\"slot02\":false}";
static const char SSL_CA_PEM[] = "-----BEGIN CERTIFICATE-----\n"
                          	"-----END CERTIFICATE-----";


int init_wifi()
{
    // init the wifi module
    wifi = WiFiInterface::get_default_instance();
    if (!wifi)
    {
        printf("ERROR: No WiFiInterface found.\n");
        return -1;
    }

    // connect to the wifi network specified in the mbed.json
    printf("\nConnecting to %s...\n", MBED_CONF_APP_WIFI_SSID);
    int ret = wifi->connect(MBED_CONF_APP_WIFI_SSID, MBED_CONF_APP_WIFI_PASSWORD, NSAPI_SECURITY_WPA2);
    if (ret != 0)
    {
        printf("\nConnection error: %d\n", ret);
        return -1;
    }

    printf("Success\n\n");
    return 1;
}

void send_update(bool free)
{
    NetworkInterface *network = (NetworkInterface *)wifi;

    HttpsRequest *request = new HttpsRequest(network, SSL_CA_PEM, HTTP_PATCH, "https://*.firebaseio.com/parking1.json");
    request->set_header("Content-Type", "application/json");

    HttpResponse *response;
    if (free && !is_free)
    {
        response = request->send(slot_free, strlen(slot_free));
    }
    else if(!free && is_free)
    {
        response = request->send(slot_full, strlen(slot_full));
    }
    else{
        delete request;
        return;
    }

    printf("status is %d - %s\n", response->get_status_code(), response->get_status_message().c_str());
    if(response->get_status_code() == 200) is_free = !is_free;
    printf("body is:\n%s\n", response->get_body_as_string().c_str());
    delete request; 
    return;// also clears out the response
}

bool get_reserve()
{
    NetworkInterface *network = (NetworkInterface *)wifi;

    HttpsRequest *request = new HttpsRequest(network, SSL_CA_PEM, HTTP_GET, "https://*.firebaseio.com/reservations/parking1/slot02.json");
    HttpResponse *response; 
    response = request->send(NULL, 0);
    if(response !=NULL && response->get_status_code() == 200){
        printf("body is:\n%s\n", response->get_body_as_string().c_str());
        if(strcmp("null",response->get_body_as_string().c_str()) == 0){ 
            delete request;
            return false;
        }
        else {
            delete request;
            return true;
        }
    }
    delete request;
    return false;
}

void dist(int distance)
{
    printf("Distance %d mm\r\n", distance);
}

ultrasonic mu(D7, D6, .1, 1, &dist);

DigitalOut reserve_led(D15);
DigitalOut free_led(D14);
DigitalOut full_led(D11);
int main()
{
    mbed_trace_init();
    
#ifdef MBED_MAJOR_VERSION
    printf("Mbed OS version %d.%d.%d\n\n", MBED_MAJOR_VERSION, MBED_MINOR_VERSION, MBED_PATCH_VERSION);
#endif

    reserve_led=0;
    free_led=1;
    full_led=0;

    if(init_wifi()>0)

    mu.startUpdates();

    int free=0;
    int full=0;
    while (1)
    {
        if (free+full>4){
            if(get_reserve()){
                reserve_led=1;
            } else {
                reserve_led=0;
            }

            if(free>full){
                if(reserve_led==1){
                    free_led = 0;
                } else{
                    free_led = 1;
                }
                full_led = 0;
                send_update(true);
            }
            else
            {
                if(reserve_led==1){
                    full_led = 0;
                } else {
                    full_led = 1;
                }
                free_led = 0;
                send_update(false);
                
            }
            free=0;
            full=0;
        }

        mu.checkDistance();
        int distance = mu.getCurrentDistance();
        
        // check if values are correct (50cm <--> 4m) and take as free only values higher than 2m
        if(distance<4000 && distance>500){
            if(distance>2000){
                free+=1;
            } else {
                full+=1;
            }
        }
        wait_ms(1000);
    }

    wifi->disconnect();
    printf("\End of the Execution\n");
}
