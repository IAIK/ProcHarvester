import subprocess
import os.path

'''
Uses gplaycli from https://github.com/matlink/gplaycli to download apps
Configure your gmail address and password in /etc/gplaycli/credentials.conf
'''

# The commented packages need to be installed manually
APPS_TO_DOWNLOAD = [

    # Google apps
    # "com.google.android.music",
    # "com.android.chrome",
    # "com.google.android.gm",  # google mail
    # "com.android.vending",  # google play
    "com.google.android.keep",
    "com.google.android.apps.photos",
    #"com.google.android.street",
    "com.google.android.apps.docs",
    "com.google.android.deskclock",

    # Utility
    # "com.dropbox.android",
    "at.DiTronic.androidgroup.randomgallery",
    # "com.mobisystems.office",

    # Social
    "com.facebook.orca",
    # "com.facebook.katana",
    "org.telegram.messenger",
    "com.whatsapp",
    "com.skype.raider",
    "com.Slack",
    "com.twitter.android",
    "com.instagram.android",
    "com.tinder",
    # "com.snapchat.android",

    # News
    "com.ted.android",
    "com.cnn.mobile.android.phone",
    "bbc.mobile.news.ww",
    "com.aastocks.dzh",

    # Entertainment
    # "com.netflix.mediaclient",
    "com.google.android.youtube",
    "com.spotify.music",

    # Shopping
    "com.amazon.mShop.android.shopping",
    "com.priceline.android.negotiator",
    "com.groupon.redemption",
    "com.ebay.mobile",

    # Games
    # "com.nianticlabs.pokemongo",
    # "com.king.candycrushsaga",
    "air.com.hypah.io.slither",
    "com.sometimeswefly.littlealchemy",
    "air.com.hoimi.MathxMath",

    # Financial
    #"com.bankofamerica.cashpromobile",
    "com.chase.sig.android",
    "com.isis_papyrus.raiffeisen_pay_eyewdg",
    "com.paypal.android.p2pmobile",
    "io.silvrr.silvrrwallet.hk",
    "com.scb.breezebanking.hk",
    "com.money.on",
    "jp.united.app.kanahei.money",

    # Health and fitness
    "com.healthagen.iTriage",
    "com.medscape.android",
    "com.mysugr.android.companion",

    # IEEE Interrupts 2016 paper
    "tv.danmaku.bili",
    "com.hket.android.ctjobs",
    "com.hse28.hse28_2",
    "com.htsu.hsbcpersonalbanking",
    "com.imdb.mobile",
    "com.indeed.android.jobsearch",
    "com.intsig.BCRLite",
    "com.intsig.camscanner",
    "com.jobmarket.android",
    "com.jobsdb",
    "com.Kingdee.Express",
    "com.kpmoney.android",
    "com.lenovo.anyshare.gps",
    "com.linkedin.android.jobs.jobseeker",
    "com.magisto",
    "com.malangstudio.alarmmon",
    "com.microsoft.hyperlapsemobile",
    "com.microsoft.rdc.android",
    "com.miniclip.agar.io",
    "com.mmg.theoverlander",
    "com.mtel.androidbea",
    "com.mt.mtxx.mtxx",
    "com.nuthon.centaline",
    "com.openrice.android",
    "com.roidapp.photogrid",
    "com.sankuai.movie",
    "com.smartwho.SmartAllCurrencyConverter",
    "com.smule.singandroid",
    "com.surpax.ledflashlight.panel",
    "com.tripadvisor.tripadvisor",
    "com.zhihu.android",
    "ctrip.android.view",
    "io.appsoluteright.hkexChecker",
    "sina.mobile.tianqitong",
    "tools.bmirechner",
    "tw.com.off.hkradio",
    "cmb.pb",
    "cn.etouch.ecalendar.longshi2",
    "com.airbnb.android",
    "com.ajnsnewmedia.kitchenstories",
    "com.antutu.ABenchMark",
    "com.baidu.baidutranslate",
    "com.baidu.searchbox",
    "com.booking",
    "com.citrix.saas.gotowebinar",
    "com.coolmobilesolution.fastscannerfree",
    "com.csst.ecdict",
    "com.dewmobile.kuaiya.play",
    "com.douban.frodo",
    "com.facebook.pages.app",
    #"com.facebook.work",
]

SUB_FOLDER = "apks"

def download_app(package_name):
    if os.path.exists(SUB_FOLDER + "/" + package_name + ".apk"):
        print(package_name + " already exists")
    else:
        print("Download " + package_name)
        subprocess.call(["gplaycli -d " + package_name + " -f " + SUB_FOLDER], shell=True)
    print("\n")

def main():
    for app in APPS_TO_DOWNLOAD:
        download_app(app)

main()
