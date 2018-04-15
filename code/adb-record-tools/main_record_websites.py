import logging_functions as lf
import randomized_record
import time
import config

RECORDS_PER_WEBSITE = 8

CHECK_CONFIGURATION = False
BROWSER = "com.android.chrome"

TARGET_WEBSITES = [
    "http://www.google.com",
    "http://www.facebook.com",
    "http://www.baidu.com",
    "http://www.wikipedia.org",
    "http://www.yahoo.com",
    "http://www.reddit.com",
    "http://www.qq.com",
    "http://www.taobao.com",
    "http://www.amazon.com",
    "http://www.tmall.com",
    "http://www.sohu.com",
    "http://www.live.com",
    "http://www.vk.com",
    "http://www.instagram.com",
    "http://www.sina.com.cn",
    "http://www.360.cn",
    "http://www.jd.com",
    "http://www.linkedin.com",
    "http://www.netflix.com",
    "http://www.imgur.com",
]


def start_website(url, trigger_event=True):
    if trigger_event:
        lf.trigger_new_event(url)
    lf.adb("am start -a \"android.intent.action.VIEW\" -d \"" + url + "\"")


def record_website(url):
    start_website(url)
    time.sleep(config.DELAY_AFTER_LAUNCH)
    lf.kill_app(BROWSER)
    time.sleep(config.DELAY_AFTER_KILL)


def check_config():
    for url in TARGET_WEBSITES:
        print("\nAttempt to launch " + url)
        start_website(url)
        time.sleep(1)


def main():
    print("Record website starts\n")

    lf.start_logging_procedure()

    # We need to perform website launches before the first actual event trigger to enable
    # the automated detection of side channel candidates
    start_website(TARGET_WEBSITES[0], trigger_event=False)
    time.sleep(2)
    start_website(TARGET_WEBSITES[1], trigger_event=False)
    time.sleep(2)
    start_website(TARGET_WEBSITES[2], trigger_event=False)
    time.sleep(2)

    if not CHECK_CONFIGURATION:
        randomized_record.acquire_data_randomized(TARGET_WEBSITES, RECORDS_PER_WEBSITE, record_website)
    else:
        check_config()

    lf.stop_logging_app()


main()
