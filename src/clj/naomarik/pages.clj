(ns naomarik.pages
  (:require
   [naomarik.env :as env]
   [clojure.java.io :as io]
   [clojure.edn :as edn]
   [clojure.java.shell :as sh]
   [hiccup2.core :as hic]))



(defn -image-dims [path]
  (let [path (format "resources/build/%s" path)]
    (edn/read-string (:out (sh/sh "./webpimagesize" path)))))

(def image-dims (memoize -image-dims))

(def ^:private favicon "data:image/png;base64,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")

(defn- main-nav [page]
  (into [:nav
         (map (fn [[href title page*]]
                [:a {:href href
                     :class (when (= page page*) "active")
                     } title])
              [["/" (if (= @env/mode :dev) "/dev" "Home") :home]
               ["/projects" "Projects" :projects]
               ["/verbiage" "Verbiage" :verbiage]
               ["/about" "About" :about]])]))

(def ^:private css (slurp (io/file (str "build/css/style.css"))))

(def ^:private *hits (atom 0))
(defn render [page & [opts]]
  (let [{:keys [nav req page-desc title]} opts
        title (or title "@Naomarik")
        page-desc (or page-desc "@Naomarik's Portfolio Site")
        boosted? (= "true" (get-in req [:headers "hx-boosted"]))
        build @env/mode
        {:keys [uri]} req canonical (format "https://naomarik.com%s" uri)]
    (swap! *hits inc)
    (str
     "<!doctype html>"
     (hic/html
      [:html
       {:lang "en"}
       (-> [:head
            [:meta {:charset "UTF-8"}]
            [:link {:rel "canonical"
                    :href canonical}]
            [:link {:rel "preconnect"
                    :href "https://fonts.gstatic.com"
                    :crossorigin true}]
            [:link {:rel "preconnect"
                    :href "https://fonts.googleapis.com/"}]
            [:meta {:name "description" :content page-desc}]
            [:meta {:property "og:title" :content title}]
            [:meta {:property "og:description" :content page-desc}]
            [:meta {:name "viewport"
                    :content "width=device-width, initial-scale=1, maximum-scale=3, viewport-fit=cover"}]
            (when (= build :dev)
              [:link {:href (str "/dev-css/style.css")
                      :rel "stylesheet"
                      :type "text/css"}])
            [:title title]]
           (cond-> (not boosted?)
             (into (-> [[:link {:href favicon :rel "icon" :type "image/x-icon"}]
                        [:link {:href (str "/css/fonts.css")
                                :rel "stylesheet"
                                :media "print"
                                :onload "this.rel='stylesheet'"
                                :type "text/css"}]]
                       (cond-> (= build :prod)
                         (conj [:style (hic/raw css)]))))))
       [:body
        ;; {:hx-boost "true"
        ;;  :hx-push-url "true"}
        (main-nav nav)
        [:div#page
         page]]
       #_(when-not boosted? [:script {:src "/js/index.min.js?ver=1"}])
       (when (= build :dev)
         [:script {:src "https://livejs.com/live.js"}])]))))

(defn img-with-caption [{:keys [src caption height width]}]
  [:figure
   {:style (cond-> {}
             height
             (assoc :height (str height "px"))
             width
             (assoc :width (str width "px")))}
   [:a {:href src
        :target "_blank"}
    [:img (merge {:src src
                  :alt caption}
                 (image-dims src))]]
   [:figcaption caption]])

(defn youtube-embed [url caption]
  [:figure.youtube
   [:iframe {:width "560",
             :height "315"
             :src url,
             :title "YouTube video player"
             :frameborder "0",
             :allow "accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share",
             :referrerpolicy "strict-origin-when-cross-origin",
             :allowfullscreen true}]
   [:figcaption caption]])

(defn about [req]
  (render
   [:div#about.container.small
    [:h1 "About"]
    [:p "A small tidbit about myself."]
    [:section
     [:h2 "The Human"]
     [:p "Love delicious food, video games, cycling, archery, and playing with my kids. Hobbies are always expanding. Californian, Muslim since 2007."]
     [:p "I'm a fairly private person publicly and don't do social media."]]
    [:section
     [:h2 "The Developer"]
     [:p "I've been doing fullstack development since 2011, working directly with CEO types before that title means anything to either of us. I've taken a lot of advice to
heart from Paul Graham, reading his essays since 2014 trying to make this startup thing work for myself. I've experienced the highs and lows of startup life multiple times, survived on
little until investment was secured and built things starting from the core problem outwards."]
     [:p "I believe in building something people want that enhances their experience in life.
Solving business problems in an impactful, robust way that lasts. Selling gimmicks and hype does not stand the test of time."]
     [:p "To more easily achieve the ever demanding need to mold software towards the requirements of reality, I have progressed from PHP to primarily using Clojure."]
     [:p "I would consider myself a debugger more than anything else. When tasked with something I don't know how to do, I have a seemingly endless amount ways of tackling the problem from every angle.
This has led me to be able to tackle objectives without direct help, building upon my experience to solve future problems and become better at scanning available resources to pinpoint exactly what I need."]

     (img-with-caption
      {:src "/img/about/bike.webp"
       :caption "Remnants of my GSXR1000, an event that would set the trajectory I am on today"})]
    [:section
     [:h2 "The Site"]
     [:p "After exploring various static site generators, I decided to just make this from scratch with clojure. It's using http-kit and babashka, served behind nginx and Cloudflare."]
     [:a {:href "https://github.com/Naomarik/naomarik-site"} "source on Github"]]]
   {:nav :about
    :req req}))

(def writing
  [{:title "DDD: Data Driven Development"
    :date "June 14, 2024"
    :slug "ddd"
    :draft true
    :content
    [:div.content
     [:p "When I have a problem to solve, I take all the necessary bits of information and the relationship between them and
model them with vectors, maps, and sets. I attempt to do this in a way that looks aesthetically pleasing and is
as small as possible. I typically write out some dummy data exploring what a real scenario would look like.
I then think about the kind of 'consumption' the data structure needs in order to be used
and might end up writing a 'compiled' version that is denormalized but extremely performant."]]}

   {:title "Bank Project"
    :date "June 23, 2024"
    :slug "twitter-banking"
    :draft true
    :content
    [:div.content
     [:p "One of the most interesting projects while I was working for a now defunct startup called Brndstr was for a major bank in UAE. We were to build them Twitter Banking which allowed users to register their accounts through a process then send direct messages to their
Twitter account and receive responses like their account balance or last five transactions."]
     [:p "The problem description was as easy as I stated and they had a list of exactly what they wanted, if I recall correctly 10 or so. That was for Phase 1 of the project due first. Phase 2 would have another set of commands and responses, presumably because it would take more hours to implement."]
     [:p "They first wanted to see if we were technically capable of doing such a thing, so they invited us to bring a computer into their main office and proudly presented us with an ethernet cable to plug into their network, gave us a piece of paper with some IP address and some instructions that none of us
could decipher someone will be with you in 4 hours to answer your questions so good luck.
It was me, one Rails developer colleague, and the CEO. We were completely dumbfounded, having no idea what to do."]
     [:p "After waiting four hours the guy finally came to us also seemingly confused why he was there."]
     ]}



   {:title "Throwing oneself at work"
    :draft true
    :date "tbd"
    :slug "work"
    :content
    "
I've noticed when people grow up and become obsessive about something, sometimes they are escaping something that traumatized them growing up. A cliche is the story of a successful entrepreneur who has worked exceptionally hard his way out of poverty. Another example are Youtubers Kitboga and CoffeeZilla, who have had family that were scammed so they became obsessed with exposing scammers.

It's like a rubberband stretching far into one direction the person hates, then when a block is lifted that allows the person to finally act freely to solve their problems, it shoots off propelling itself into the opposite, disproportionately travelling into the opposite direction far further than it was originally stretched.

I grew up with a single mother working fulltime. She did her best and provided well for my sister and myself, being part of upper middleclass. That also meant she wasn't around much to take care of us and that was left to daycares and babysitters. I spent an enormous amount of time growing up in situations I did not want to be in because I had no control over my environment. I always wanted to flee as fast as possible to

I used to value speed over all else, but as I've become older, I've realized hitting a target faster doesn't necessarily lead to the bullseye. I now value plans and correctness more than raw execution speed
because over time it leads to faster execution.


Discipline <-> Pleasure, also Discipline -> Pleasure. Both a tug relationship and a push relationship where discipline can lead to pleasure. Enjoying discreet moments of life, cashing out on
what you've worked for.

Exerting will over someone.
{:craziness-level ;; is someone willing to shoot you through their own foot, or in tekken yoshimitsu's attack where he stabs you through himself}

:
{:task
{:goal ;; may not be to complete entire thing
 :method ;; actual work being done
}
:plan
:reward {}
:motivation
{
:failure-consequences
:life-purpose
:what-this-provides
:long-term-reward
}
:distractions {:internal #{:stress :upcoming-interruptions} :external }
:engagement/working-on-task ;; whittle away at ability to execute
{:task-fatigue
:flow ;; execution momentum
:mana ;; ?
}
:skill ;; modifies engagement, multiplier to frustration/task fatigue and flow. bell curve, strike a balance. too easy and too hard is negative
:discipline ;; defense against distractions https://www.youtube.com/watch?v=0kIoZi3yxYk
}
;; apply this to real tasks and games


;; mental state
{:burnout {:short-term :long-term}
:energy
:discomfort

}

;; self-alignment
{:ethics {:religious
:culturual
:self}}

;; within
{:perceived-importance
:in-practice}


Something about entertainment, movies, comedy, games to refill/reduce burnout.
Something in here about being stubbornly stuck to a problem. maybe failure-consequence, with self inclination of not allowing a problem to win.

Working on something you don't believe in can decrease an attribute adding to your burnout
"}

   #_{:title "Linux Desktop Assessment"
      :date "October 07 2024"
      :slug "linux-desktop-assessment"
      :content
      [:div.content
       [:p.preline
        "The point of this post is mainly to log my thoughts after looking into linux distros for a bit so I can remember why I made my decision to stick with Mint.

NixOS: Looks cool to configure reproducible but people seem to struggle a lot with syntax. I don't have time for that. Also no FHS seems to have caused problems with people. Complaints about arcane error messages.
Arch: I used this back in 2011 for about 3 weeks and an update broke my system. That still seems to be common. Also my servers all use apt and using pacman is horrible.
Fedora: I'd rather use apt than dnf, but looks great if I wanted a Windows-like experience.
Ubuntu: Don't want snaps, dislke their theme.
Kubuntu: Apprently this is really good on resources, but still don't want snaps. Might be worth checking out.
Zorin: The presence of a paid version makes the free version feel freemium. Not sure if this the case in practice. Supposed to be good and performant.
MX Linux: Extremely light weight, no ubuntu repos.
Debian: Old packages.
PopOS: Theme looks nice, but installed it on an old laptop and performance was bad compared to mint.

KDE at this point in time is beautiful but I don't need it due to maining i3.

And the distro I chose to stay,
Linux Mint: While I don't care for cinnamon, i3 on it works well. Timeshift has already been useful for me within a week of using it and gives me confidence to do risky things I know I don't need to spend time fixing. Also ubuntu base means it's close to how I run my servers. Performance is great and resource usage OOB is very low even on cinnamon.
"]]}

   {:title "My thoughts on AI/LLMs"
    :date "Nov 7, 2024"
    :slug "ai-thoughts"
    :content
    [:div.content
     [:p.preline
      "With frameworks, basic CRUD apps have been extremely easy to build for a very long time. I jumped into Rails in my second year programming and was able to throw MVPs together in no time. Libraries exist for almost everything you'd want to do. It just becomes a matter of gluing code together to build something. One of the best resources at the time was Railscasts, a long running series with more than 400 videos showing exactly how to consume libraries to do various tasks.

While I was working in Rails, I noticed quickly that the problem space my colleagues and I could solve were dependent on the libraries that were available. This only became an issue for me when I started trying to do more complicated things. I would routinely see my colleagues being able to discuss what's possible with the CEO on their knowledge of the libraries and frameworks they used, and it was extremely limiting. I remember my first challenge that did not have a library available was figuring out how to create a chat bot that acted in a very specific way and could branch to different conversation paths dependending on what your answers were and validate input.

A novice programmer would just use cond/if/else branches and conversation paths get more complicated, it becomes an enormous mess very fast and prone to errors.

I intuitively knew there was a better way, but had no idea how to reach it. It took me about two weeks of bashing my head against the problem to be able to build a map of nodes that described various branches and what triggered their conditions. This allowed me to rapidly build any chat bot I wanted which helped my cofounder and me immensely in securing projects at the time. The best bang for the buck I got was a bot I delivered for a semi-government department that we got 80k for with less than one week of work. It came with a backend that would list all entries completed and used both Twitter DMs and Telegram to consume the same conversation data structure. This was demo'd prominently at their large booth at GITEX.

When asking AI for code, you can be as specific or vague as you want. If you're non technical you won't even know when you're being vague. You can ask to code a full program or a specific piece to generate algorithm to manipulate data to your requirements. This is can be both extremely beneficial and harmful.

The benefits come when you're making easy things. Since these LLMs are trained on public (hehe) data, it's great for spitting out code that has been solved thousands of times before.

In the short term, you reap rewards of getting more done quickly. In the long run, you end up with a poorly engineered codebase with no architectural oversight. Since many people were bad at programming before generative AI, this isn't really a big problem. The barrier to entry to building things has been significantly lowered now for most people which is great. So low that one of the most insane things I stumbled upon was an indie dev who managed to monetize react-toast notifications as a SaaS.

Here's where I see the problem though. If you don't need to think much about the problem you're solving, you lose problem solving abilities. A lot of coding for people relying on AI becomes a back and forth of giving it error messages for the code it just spat out.

Just like the components of an automobile, the components of a codebase have to interact with each other. Imagine a car where the air conditioner was built in complete isolation, requiring its own power source instead of tapping into the power system of the car. As a driver of the car you might not know or care, but as more components are built like this, it leads to an extremely poorly designed vehicle that cannot leverage its own system.

This is analogous to technical debt in codebases, and without oversight of an experienced software architect, you'll end up with a lot of bloat, jank, and poor performance in the long run since nothing is composed together very well. When it comes time to solve a performance issue or tweak a feature ever so slightly due to changing requirements, you might have to rewrite the entire thing which can break other pieces of the system and might not manifest itself until specific conditions are met.

The state of a running running web platform is highly dependent on the data that flows through it, and once a platform is launched and starts populating a database with records of user accounts, billing information, and everything else, the platform needs to be able to gracefully handle that data forever.

This can only be done when the people working on the platform can hold a conceptual model of the entire context of the platform the part of the system being worked on if the implementation details are isolated well enough from its public APIs.

The idea that AI will completely replace programmers is nonsense and will never happen. If that's the case, the non technical CEO of a company no longer needs a CTO, or project managers, because they can just describe what they want and get it. That won't happen because the output prompting an LLM with few sentences will generate pages of code that still has to be compiled or interpreted by the computer which is nuanced precisely as instructions with no ambiguity, but ambiguity is inherent in the intent due to the generative nature of this action. This ambiguity of intention will cause unforeseen problems in the future, especially when trying to iterate on an existing codebase.

Awhile back I was pairing with a programmer that had copilot enabled in VSCode and as he typed it would autocomplete large swathes of code that completely distracted us from our intentions, like a know it all constantly trying to blurt out what you're going to say before you've finished your sentence. If this is useful to anyone, they're not really good to begin with or using a poor language/framework that requires too much boilerplate ceremony.

The biggest advantage I've come to get from AI is that it has reduced friction immensely in working in other languages I'm not familiar with. Instead of looking up syntax, I can quickly ask for a quick example. It has essentially become what Google has used to be back when search results weren't encrappified.

It has also been extremely useful in learning things or getting a curriculum of what to look up when I'm asking for something far outside my knowledge.

I've had moments where I've attempted to use it in lieu of using my problem solving skills and I've never ended up with anything useful. This is due to experience I have and having a strong opinion on how things should be, so I'm much faster at just writing the code I intend in the first place.

In conclusion, I would have never imagined computers were capable of giving us this. I think it's extremely beneficial when you know its tradeoffs, but good luck to anyone inheriting a codebase that was generated with wide and vague intentions. Robotic factories have largely replaced the human assembly line, but mechanics and their knowledge are still required to fix automobiles. If used all the time as a crutch, one will lose the ability to walk."]]
    }

   {:title "macOS -> WSL2+Ubuntu -> Mint+i3"
    :date "Sept 30 2024"
    :slug "dev-env-update"
    :content
    [:div.content
     [:p.preline
      "I currently have two laptops: a 7th generation Lenovo Legion Slim 7i and a 16 inch M1 Max MacBook Pro. I've been using WSL2 within Windows for a few years using X410 for Doom Emacs. For some reason I do not reach for my MacBook even though in many ways it's the superior laptop. Speakers are better, the battery life is better, and my development tools are a bit snappier and more stable. Using WSL2, I often face glitches daily with my X410/Emacs setup where I have to stop my work because the shift key is permanently enabled or it'll just freeze.

I think the main reason I don't like the Macbook is the feel of it. When I say think, it's hard to pinpoint why because the device just works, but something subconsciously repels me from using it. Typing on it compared to my Lenovo just feels cold, with sharp edges and not very tactile. My Lenovo's plastic construction never gets cold and the edges are chamfered so they don't dig into my wrists. The Mac's screen is also inferior being highly reflective, noticeable ghosting due to poor pixel response time, and half the time the scrolling through apps is noticeably 60hz despite 120hz 'Promotion' being perpetually enabled. The device also only has 512gb of storage with 32gb of ram and cost thousands of dollars when it was new. My Lenovo has 40gb of RAM and room for 2 SSDs so I've got a total of 3TB and was less than half the price.

Through the use of AutoHotkey, Raycast, and Rectangle both have the same hotkeys that allow me to invoke my browser, editor, terminal and window management without using the mouse.

MacOS at some point has become extremely annoying to use. From 2012-2018 it was my only option to get work done. At some point I was getting constant iCloud notifications that I could not disable nagging me to upgrade my storage so I ended up just deleting everything it had synced altogether.
A default mac freshly unboxed is one of the worst computing experiences I've ever witnessed. Watching other people use it, clicking on the extremely small green button to maybe maximize or fullscreen a window and having 6 fullscreen programs open at once they have to swipe through with gestures. Watching a non technical person attempt to multitask on a Mac gives me a similar sensation of being helplessly stuck sitting in heavy traffic. Apple devices provide an austere experience that is hard to break out of. Everyone's used to it, but it's still insane to me that you have to pay them money to develop on their platform.

Windows has its own issues. It feels like public transport where anyone is allowed to get on. Even after debloating it feels like the OS has things going with cpu spikes and random network activity. My power consumption on idle fluctuates wildly because every program has free reign to do what it wants and every major windows update is like a trojan horse for more services that run in the background that I don't know about. Inspecting the services and processes is unhelpful because the legitimate services that are harmless sound like poorly named viruses. With all the advertising and now Windows Recall being injected into everyone's veins constantly recording your screen it no longer feels like the operating system serves the operator.

In the past several weeks I've dealt with a neverending stream of interruptions due to unavoidable life situations that has derailed my velocity in making meaningful progress for work, so I decided right before I get back into it I would try Linux on my Lenovo. The last time I installed linux on bare metal was 15 years ago and had a working Arch setup for about 3 months before an update broke everything.

I deleted some games from Steam, used gparted from the Linux Mint live USB to shrink enough space to partition a drive and installation went smoothly. Out of the box sound wasn't working and my external 4k monitor was not able to run at 120hz. I solved my sound issues by updating my linux kernel to 6.11 and after adding some graphics ppa to install the proprietary nvidia 550 drivers my 4k monitor ran well at 120hz. My other major issue was the trackpad had no palm rejection, so when I typed anything I would constantly lose focus from wherever my cursor was making it completely unusable. I solved this by installing synaptic drivers and adding a deadzone to the edges. I also set up i3, something I've been wanting to try for the last decade. Configuration and the experience usage is a lot easier than I thought it would be, exceeding my expectations. I've never experienced multitasking on this level and haven't had the desire to boot back into Windows.

The resource consumption is ridiculously low. A cold boot into i3 consumes no more than 1 gig of RAM and applications launch with no delay or animation lag. I never felt my computer was slow using Windows, but the gap in performance is actually very wide. Idle power consumption is very stable and low at 5w, no random spikes of CPU usage when not in use. i3's default status bar has a battery power consumption field that can be enabled and the system constantly falls back into minimal power consumption as soon as my hands are off the keyboard. This has never been the case for me in Windows.

The other major thing I wasn't expecting is my laptop's sleep actually works. I've had at least a dozen moments I've thrown my laptop in my bag only to pull it out as if it's been in a toaster. I've tried every tweak to solve this because it's a known issue but nothing has worked. On Windows, if I wanted to go to a cafe I had to completely shut down to avoid any issues, which is a horrible laptop experience.

ChatGPT was extremely helpful in configuration and pointing me to the correct programs and documentation to look up. I could hardly find anything useful with Google and has become more of a glorified address bar so I don't have to type out URLs.

Something unexpected that I'm experiencing from my new setup is that my computer now feels like a device that is obedient, doing nothing clandestine. I'm no longer being advertised to, my computer is not wasting resources constantly performing background tasks I cannot observe. I don't have to worry about AI integration that I absolutely don't want. The operating system serves its operator and no one else."]]}

   {:title "Crowdstrike and the criticality of data validation in maintaining integrity in software systems"
    :date "June 26, 2024"
    :slug "crowdstrike"
    :content
    [:div.content
     [:p "A windows developer made a video explaining why all windows computers using Crowdstrike bluescreened. In short, they have code running on windows that "
      [:strong [:i "blindly"]]
      " takes any update they push from their servers to keep their malware protection up to date. This was pushed to every computer at the same time from Crowdstrike's servers and was allegedly full of garbage data that their windows driver failed to check the correctness of before subsuming it."]

     [:p "Notice how I said blindly. He goes on to explain that if they did any kind of parameter validation that this kind of thing could have never happened. When creating software that can take random user input, or in the case of Crowdstrike even their own updates, anything that changes the state of a running program should be validated before accepted."]

     [:p "This kind of issue has been well documented in various forms, SQL injection being one of them. Ensuring the integrity of a live system does not just depend on protecting against common security mistakes, systems also need to have granular data validation about every single input that a user has access to. How large can a number be? How many characters can a description contain? What are the valid characters of a user handle? Given two sets of data, how are they valid with each other? I actually saw code in booking platform that allowed you to set the end time lower than the start time for booking, displaying a negative price to the user."]

     [:p "Take a description field in a web form. You might be limited to the amount of text you can write where the UI will give you an error. The backend however might store that description text as an unbounded column in a database table. If the backend just accepts the data to be saved from the client relying it fully to be constrained, you run the risk of a malicious person being able to craft an API request to potentially save the entire English dictionary. If that is done enough times to a database, it can easily cause a system disk to run out of space bringing a platform down. Or even worse by blowing up the bill of a managed database instance to the point of being unpayable." ]

     [:p "I remember back around 2020ish, a user was able to crash Instagram for anyone visiting his profile page. The reason is that at the time Instagram was relying and blindly accepting their mobile app to specify the image dimensions being uploaded. The user managed to intercept those API requests and change them to absurdly large values which Instagram blindly committed to its database and pass on to its users. So instead of trying to render an image with a sane width of lets say 1000px width or height, the dimensions were in the hundreds of trillions of pixels, leading to the crash."]

     [:p "Most web stacks are not capable of using the same validation code in the backend that the frontend uses. It's something that has to be coded once for the client and again for the backend, and that's tedious. Since the time of creating Booma, my first clojure project, every single piece of user input that gets saved is validated with the same code used on both the client and the backend. Clojure makes this extremely easy with .cljc files that allow you to share code that works on both frontend and backend."]

     [:p "Malli, a data validation library in Clojure, makes specifying data extremely easy by providing probably the most terse syntax possible in our reality to define the shape of data while also being programmable and composable. This has been my go to for everything since before it even had a version number. With malli I'm able to write my validation code once to both show users errors on input in the UI and gatekeep input into my database to reject any invalid data before it gets transacted. Data validation ensures that web platforms behave and act in the way intended by rejecting bad data and ensuring the overall integrity of a running system."]

     [:p "There's also a practice of generative testing, which I seldom see talked about, which is test code that stresses the limits of your code by continously passing in the most random sets of input possible. I vaguely recall a talk given by a software engineer that worked on code for an automobile that uncovered a bug only discovered with this method."]

     [:p "With regards to liability, if you're making a software company and you do not understand all the ins and outs of your system, it is solely the fault of the leadership to accept the responsibility for their problems. They chose who to hire, they decided how much time they spent trying to understand their tech, and they reap all the rewards.

A company deploying silent updates to a low level kernel driver at will, if set up properly, should have been so robust that a monkey could have bashed at a computer and deployed any permutation of data even bypassing all checks that may have happened pre-flight."]

     [:p "When relying on external input or updates, you should always assume the worst intent, and write code to handle it gracefully."]

     [:div.center
      (youtube-embed "https://www.youtube.com/embed/wAzEJxOo1ts?si=DN0O-kkSWHMnwWEw"
                     "Windows Developer Explains Crowdstrike BSOD")]]}




   {:title "Being a 19 year old rascal in a corporate health care consortium"
    :date "June 15, 2024"
    :slug "corporate-rascal"
    :content
    [:div.content
     [:p "Back when I was a wee kid I managed to get a job at Kaiser Permanente. My job description officially was"  [:i " Business Application Coordinator"] " or BAC and they hired a bunch of people at once
in the who lived near bay area. My youngest colleague was 25 years old graduated with an Arts degree, followed by a 27 year old IT specialist. The average age of my colleagues was probably late 30s most
having already started a family, so I naturally gravitated towards the two closest to my age."]

     [:p "The premise of the role was that Kaiser has been using some legacy 1980s text based system for their entire hospital software, and it was time to upgrade to
a nice modern GUI, with drag and drop features, layouts representing physical spaces of the hospital, interconnecting all systems, departments, handling all aspects of
registration down to charting and viewing EKGs and x-rays. They were upgrading to " [:a {:href "https://www.epic.com/"} "Epic"] ", a behemoth of a system still standing today."]


     [:p "We BACs were the frontline support standing alongside all hospital staff when they flicked the switched on in a staggered roll-out through California. My cohort had about 35 people."]

     [:p "I attribute getting hired because I haad been teaching my neighbors and family friends how to use computers and software since I was a child, so I had many years of experience for this role."]

     [:p "The job had three months of intensive training in mainly held in Emeryville with various instructors covering all the topic we needed to know. We were to understand intimately the entire
workflow of all hospital staff and how they interact with the software, as well as the quirks that existed. This training was extremely boring to me because the UX was intuitive and I picked up on the workflows very fast."]

     [:p "One type of training was a live demo in a large room filled with rows of desktop computers facing a presenter in a classroom setting. The software was sync'd across every PC in real time
including the instructor, which all eyes were on."]

     [:p "One day our instructor was a very serious tall well groomed man. One of my younger mates was sitting a few rows behind me where I could clearly see his face."]

     [:p "I fondly remember this day because I managed to discover some networking admin commands and used them targeted to his PC to do random things. The first was a command that would alert him
his computer was restarting at a time specified by me, which I could abort. The look on his face of sheer confusion made it extremely hard to contain myself and I was getting stern looks by the
super serious instructor who had no idea what was going on. I would end up aborting the mandatory reboot right before it happened."]

     [:p "At some other point I managed to find a folder of the most contrived silly stock photos were on every PC. I would issue commands to open these images and delight in reactions."]

     [:p "One day we had training for ER. An extremely jovial lady who was a blast to have as instructor had a sur-name of McCoy. In front of the entire classroom, we had a live view of the current
state of emergency department (ED) patients in our system, and we could all edit them in real time. I edited various patients to have a surnames that included my friend's hypenated
with the instructor, so for instance Butler-McCoy or Bond-McCoy and gave them chief complaints of some common STDs."]

     [:p "They picked up on this and we started having a battle of who could one up each other on silly chief complaints like exploive projectile diarrahea and going as far as to use our entire
real names. Most people were completely oblivious to what we were doing or said nothing and the instructor just thought we were enjoying her class way too much."]

     [:p "One morning I was getting coffee with my friend in Jack London Square, standing in line and recounting my various deeds. A man behind us overheard what I was saying and
confronted me telling me he was an admin working there and had no idea that all the computers had admin access to each other."]

     [:p "Corporate email was also not secure from my shenangians. At point we were cross coordinating with another team of about 50 people. One young woman
sent us some email that had a benign grammar mistake. I went to my colleague and told him I would buy him two tacos if he replies with an e-mail pointing out the mistake. He refused but I did
not relent. He finally acquiesced to my bribe of 4 tacos and with my help we composed a reply, CCing the entire list pointing out the grammar mistake. It's so petty but it still makes me giddy
writing about it."]

     [:p "My very first deployment we were all shadowing experienced BAC veterans in Walnut Creek. One nurse looked up, saw me and " [:i "Aren't you too young to be working in the emergency department?"]]

     [:p "Among all my colleagues everyone came to me for help when they had issues they couldn't resolve. We were deployed for months at various Kaiser hospitals in California and I was always stationed
    in the emergency department because the doctors and nurses there had zero patience to deal with support that wasn't quick."]

     [:p "I made many friends during that time, and my most memorable deployment was at the Kaiser stationed inside San Francisco. My young self made friends with everyone and would routinely bring Starbucks
and sometimes pho back for the staff."]

     [:p "Socially the job was a blast. But I always felt like I had no progression because I hit the skill ceiling immediately with this work. Nothing about the job was difficult and the only thing
that kept me engaged were the people. Knowing the ins and outs of every system at Kaiser and every single workflow every staff member used was not challenging me mentally. A skill ceiling simply
does not exist in the realm of software engineering and I'm glad I left when I did, but I still miss working with great people."]]}

   {:slug "new-site"
    :title "My new custom portfolio blog scores a perfect 100 on Lighthouse"
    :date "June 15, 2024"
    :content
    [:div.content
     [:p "I've been meaning to make a portfolio site forever. Upon embarking on any project, I always scan the landscape to see what people are using.
Hugo and Jekyll are the two most well known. In the clojure space there is Cryogen, Clerk, and borkdude's quickblog."]

     [:p "I was amazed to see people recommending a new one called Astro which claims it's a frontend agnostic chimera with SSG capabilities. It's probably cool
and everything but I have no idea what purpose it serves to be able to swap out frontend frameworks like react and svelte instead of just choosing one.
I don't know how anyone is supposed to learn to be proficient in anything when new frameworks and build tools come out at breakneck speed,
with changes breaking APIs and its users' sanity as they inevitably evolve."]

     [:p "Later I started talking to "
      [:a {:href "https://yogthos.net/"
           :targe "_blank"} "yogthos"]
      " about my intent to make a portfolio site and he showed me you can make a babashka script that launches an http-kit server using hiccup and htmx in a single file.
He just pasted it to me in a DM. I was pretty blown away, reminded me of the days I could just FTP things with PHP."]

     [:p "I benchmarked what he sent me and it was spitting out hiccup generated HTML at 70k/rps. This is intepreted clojure with instant startup. For context, rails tested several months ago
on the same laptop does about 100 requests per second for a small JSON response."]

     [:p "None of the existing blogging solutions really appealed to me. Not because they're bad, but I'm lazy and don't want to learn anything especially when it's going to be more verbose than clojure. I also
have some ideas in the future to grow this site to do more complex things than a static site can do. So I ended up turning that code Dmitri pasted to me into what this is today."]
     [:p "The lighthouse score was a bit of a surprise to me. It started off not being so perfect but was still very high."]
     [:div.center
      (img-with-caption
       {:src "/img/verbiage/june-2024/lighthouse.webp"
        :caption "Never did this before, a perfect lighthouse score"
        :width 370})]

     [:p "That shows:"]
     [:ul
      [:li "100 Performance"]
      [:li "100 Accessibility"]
      [:li "100 Performance"]
      [:li "100 SEO"]]

     [:p "This is my first time using HTMX and I'm using boosted URLs on every link, which just makes an ajax request and swaps out the html instead of doing a full page load. The savings are pretty negligible and probably only barely perceptible to someone with a slow 3G connection and I'm ambivalent on whether I'll keep it this way."]
     [:p "I figured HTMX would send something announcing itself in its ajax requests and I was right, finding " [:code "hx-boosted"] " being included in the headers."]
     [:p "So I inlined all the site's CSS excluding font imports for the first page request and any boosted requests would not serve any JS or CSS."]
     [:p "I also convert all images to webp and set their original sizes in the image tag to prevent layout shifts while loading."]

     [:p "I'm used to a compile + deployment time of about a minute on my current projects for both backend and frontend. All I have to do with this is type " [:code "bb go"] "
where it'll build the css, rsync the project, restarts the process and it's all done under a few seconds."]
     [:p "Despite the score, this site still feels significantly slower than the SPAs I made previously because every page is being loaded on demand. Initial page load is much faster
but ~150ms latency is very perceptible."]
     [:div.center
      (youtube-embed "https://www.youtube.com/embed/sBzgPQ2a0bs?si=raT_5t2L3hBMABLz" "Sayartii page navs are instant after the ~1 sec initial load")]
     [:p "I could have with just as little effort made this site a snappy SPA but decided against it for optimal search engine compatibility.
Both have their tradeoffs and merits, but SPAs exist for a good reason and provide the optimal experience if done right."]
     [:p "To see everything this site is doing, you can find the source in the "
      [:a {:href "/about"} "Aboot"] " page"]]}

   {:slug "why-clojure"
    :title "Clojure for the past 9 years and forseeable future"
    :date "June 14, 2024"
    :content
    [:div.content
     [:p "Professionally it started with PHP where I was building out custom Wordpress blogs from Photoshop files. I then (re)made a very complicated online collectible virtual fantasy pet platform."]
     [:p "Upon completion of that, I didn't want anything to do with PHP ever again. I learned Rails and TDD to help me solve the problems I had with the complexity refactoring a PHP project."]

     [:p "I jumped into web development just solving other people's needs without any formal background of software engineering. At some point when working on
a side project in Rails, I realized I was a 'framework' programmer who was adept at doing things that had documentation and stitching libraries together, and so
were my colleagues. Doing anything that strayed far away from the guard rails of what was provided to me wasn't very easy."]

     [:p "Early on in my career I attended a small hackathon event in Dubai for web developers. I destroyed the first two rounds, being the first one done and went beyond and above
the requirements. Then they gave us some data in the form of csv and told us to visualize it. I froze up. This was unfamiliar territory, outside my ORM where
I could do " [:code [:i "$object"] ".all.each"] " and iterate through it. I ended up spending the entire time allocated trying to hobble together a quick CSV to ORM library
so I could work with the data in a manner that ORMs taught me to. I completely failed in doing that and ended up achieving nothing for that final round. It completely humbled
my inflated ego from my first two wins."]

     [:p "At some point I read Paul Graham's "
      [:a {:href "https://www.paulgraham.com/avg.html"} "Beating the Averages"]
      " and felt I wanted to learn a LISP. Then around 2014, I read " [:a {:href "https://www.defmacro.org/ramblings/lisp.html"} "The Nature of Lisp"] " and remember my mind exploding from the enlightenment and learning a LISP was my only way forward."]

     [:p "I had another realization. The essence of what I've been doing all these years is just data, transforming data, and threading data through APIs. That's what both backend and frontend web development is at its most boiled down substance. Anything that gets you closer to that essense is going to be a better experience in getting things done."]

     [:p "Clojure puts data and data transformations first, and all the things that were magical to me using Rails and the ruby ecosystem are plain to see in Clojure. Using an ORM for half my programming career at that point and CLI tools to generate controllers, models, and migrations was like being great at ordering fast food but not knowing how to put ingredients together and cook your own meal."]

     [:p "Object oriented programming just adds layers of nonsense to data. Instead of just taking information (a data structure) and acting on it (a function) you have to name everything, instantiate objects and twirl your in your chair reasoning, wondering, about the behavior of some object that has inherited 50 classes interspersed with public and private fields."]
     [:div.grid.col-2
      (img-with-caption
       {:src "/img/verbiage/why-clojure/oopgarbage.webp"
        :caption "Why would anyone choose this?"})

      (img-with-caption
       {:src "/img/verbiage/why-clojure/data-better.webp"
        :caption "This is all you need"})]

     [:p "While it's pretty cool that you can call code like " [:code "User.all"] ", coming from an uneducated background like I did meant that if there is no method on an object in some
library, I was in hard mode territory. I've seen this with myself and my fellow colleague working in Rails. The extreme convenience gave us initial speed but also made us incompetent and
slow when we inevitably were required to push webapps past the boundaries of out of the box solutions."]

     [:p "I cannot believe the amount of libraries in the Ruby ecosystem that exist just to call APIs of random SaaS products just to instantiate objects with just JSON data that are
mapped to fields so a junior developer could use the dot operator."]

     [:p
      [:a {:href "https://www.youtube.com/watch?v=ShEez0JkOFw"} "A talk given by Tim Ewald"]
      " explains the power of clojure by comparing it with woodworking. I've watched this twice, the second time when I started woodworking as a hobby. This talk completely matches my experience developing software. Just like in my early days, I was able to spit out models and pages at blazing speed using generators and libraries that did all the heavy lifting. But simply doing something with raw CSV data made my brain hurt."]

     [:p "Being proficient with molding data by hand makes you capable of making extremely bespoke things."]
     [:p "Since I've been using Clojure, I've transcended the limits of what I can achieve being contingent on whether or not a library or framework exists. This applies to any language,
but Clojure's essence is strong data structures and core functions to manipulate them. You also have syntax irreducibility because it's a lisp and strong performance which allows me
to run my web apps on cheap hardware. The ability to write code that compiles both on JVM and Javascript in the same file is superb for things like writing validation code once
that works on both client side forms and backend."]

     [:p "This is why for at least business web applications, I will always default to Clojure when starting my own projects."]]}])

(defn post [req id]
  (let [item (first (filter #(= (:slug %) id) writing))
        {:keys [title date content]} item
        head (str "@Naomarik - " title)]
    (render
     [:div#post.container.small
      [:div.entry
       [:h1 title]
       [:span.date date] content]]
     {:page-desc head
      :req req
      :title head})))

(defn verbiage [req]
  (render
   [:div#verbiage.container.small
    [:h1 "Verbiage"]
    [:p "Where letters are spewed forth about random topics"]
    (into [:div.entries]
          (map (fn [{:keys [title date slug]}]
                 [:div.entry
                  [:div.date date]
                  [:a
                   {:href (str "/verbiage/" slug)}
                   [:span.title title]
                   #_content]])
               (if (= @env/mode :prod)
                 (filter #(not (:draft %)) writing)
                 writing)))]
   {:nav :verbiage
    :req req
    :title "@Naomarik - Article Index"}))

(defn home-page [req]
  (render
   [:div#home.container
    [:h1 "Naomarik aka Omar Hughes "]
    [:div
     [:div
      [:p "This is where you can find up to date information on my projects and writings."]
      [:p "Get in contact: "
       [:a.btn {:href "mailto:omar@naomarik.com"} "omar@naomarik.com"]]]
     [:div
      (img-with-caption
       {:src
        "/img/home/ghaith.webp"
        :caption "Baby Ghaith"
        ;; :width 770
        })]
     [:br]
     [:div.hits (str @*hits " renders since last update. Deployed " @env/sha)]]]
   {:nav :home
    :req req
    :page-desc "@Naomarik - Home"}))

(def all-projects
  (let [project-img (fn [id img]
                      (format "/img/projects/%s/%s.webp" id img))]
    [(let [img (partial project-img "motorsaif") ]
       {:id "motorsaif"
        :title "MotorSaif"
        :tags ["Clojure" "Datomic" "PWA" "LIVE"]
        :desc "Automotive services. Make a request, get responses in the form of organized live chats from businesses."
        :thumb (img "home")
        :link "https://motorsaif.com/"
        :page
        [:div#motorsaif
         [:p "Actively developing. Trying to fix the nightmare of getting a used car fixed."]
         [:p "See it in action "
          [:a {:href "https://motorsaif.com"
               :target "_blank"} "MotorSaif"]

          [:section.grid
           (img-with-caption
            {:src (img "home")
             :caption "Home"})
           (img-with-caption
            {:src (img "requests")
             :caption "Desktop view of requests"})]

          [:section.grid.col-2
           (img-with-caption
            {:src (img "mchats")
             :caption "All requests and previews of how many people replied to each"})
           (img-with-caption
            {:src (img "mchat")
             :caption "A successful request with receipt being sent as a picture in chat"})
           (img-with-caption
            {:src (img "mreplies")
             :caption "Businesses respond to an open request. From a request owner point of view, chats are grouped and contained within the context of that particular request"})]]]})

     (let [img (partial project-img "sayartii")]
       {:id "sayartii"
        :title "Sayartii"
        :tags ["Clojure" "Datomic" "Elasticsearch" "LIVE"]
        :desc "Car classified platform, serving ~1M unique users monthly."
        :thumb (img "mnew-home")
        :link "https://sayartii.com/"
        :page [:div#sayartii
               [:section
                [:a {:href "https://sayartii.com"
                     :target "_blank"} "Sayartii.com"]
                [:p "Created and maintaining entire site. Cool features include a dealership dashboard, infinite scroll, analytics on ads."]]
               [:section
                [:div.center
                 (youtube-embed "https://www.youtube.com/embed/sBzgPQ2a0bs?si=raT_5t2L3hBMABLz" "Video demonstrating site speed")]]
               [:section.grid
                (img-with-caption
                 {:src (img "mnew-home")
                  :caption "Mobile landing page"})
                (img-with-caption
                 {:src (img "listings")
                  :caption "Listings"})
                (img-with-caption
                 {:src (img "listing")
                  :caption "Car Ad"})
                (img-with-caption
                 {:src (img "dealerdash")
                  :caption "Dashboard for dealerships to quickly manage listings and see analytics"})
                (img-with-caption
                 {:src (img "analytics")
                  :caption "Analytics each user gets. Notice the jump when ad is featured (F)"})
                (img-with-caption
                 {:src (img "backend")
                  :caption "Backend showing ads posted per day"})]]})

     (let [img (partial project-img "aceplace")]
       {:id "aceplace"
        :title "Aceplace"
        :thumb (img "home")
        :tags ["Clojure" "Datomic"]
        :desc "Booking platform for meetings and events in venues across UAE."
        :page
        [:div#aceplace
         [:section
          [:p "CTO of Aceplace for a time and made nearly this entire platform
myself before I left after company ran out of funding. Coded entire initial MVP myself that
led to our first bookings. Also hired and managed other devs."]]

         [:section
          (img-with-caption
           {:src (img "home")
            :caption "Home"})]

         [:section.grid.col-2
          (img-with-caption
           {:src (img "mhome")
            :caption "Mobile home"})
          (img-with-caption
           {:src (img "mbusiness")
            :caption "Business process"})
          (img-with-caption
           {:src (img "mlisting")
            :caption "Mobile listing"})
          (img-with-caption
           {:src (img "mbooking")
            :caption "Checkout process"})]
         [:section.grid
          (img-with-caption
           {:src (img "yachts")
            :caption "Yachts"
            })
          (img-with-caption
           {:src (img "spaces")
            :caption "Unique venues"})]]})

     (let [img (partial project-img "booma")]
       {:id "booma"
        :title "Booma"
        :thumb (img "home")
        :tags ["Clojure" "Postgres"]
        :desc "Live Chat SaaS with kanban dashboard."
        :page [:div#booma
               [:section
                [:p "First project I did in clojure. This was meant as B2B SaaS chat to help businesses deal with customers by giving them
an efficient interface to deal with livechats, allowing a ticket to be passed in between departments and tables to better direct a customer to someone that could help them.
It was used by Dubai Statistics's site for several months before I ultimately shut the site down."]]
               [:section.grid
                (img-with-caption
                 {:src (img "home")
                  :caption "Home"})
                (img-with-caption
                 {:src (img "kanban")
                  :caption "Kanban backend. Default tables were Inbox and Owned. Other boards and tables could also be created to triage customers."})
                (img-with-caption
                 {:src (img "timeline-expanded")
                  :caption "Timeline of events in the form of tickets"})
                (img-with-caption
                 {:src (img "sidebyside")
                  :caption "A side by side view of backend dashboard and customer chat"})]
               [:p "This form is dynamic in that it could ask different questions depending
on user's responses."]
               [:section.grid
                (img-with-caption
                 {:src (img "typeform")
                  :caption "A small typeform clone"})
                (img-with-caption
                 {:src (img "makingform")
                  :caption "Interface making custom forms"})
                (img-with-caption
                 {:src (img "questiongroups")
                  :caption "Creating question groups"})]]})

     (let [img (partial project-img "wantfu")]
       {:id "wantfu"
        :title "WantFu"
        :thumb (img "wantfu")
        :tags ["React Native" "Clojurescript" "Datomic"]
        :desc "Reverse classifieds. You want, you get."
        :page [:div#wantfu
               [:section
                [:p "React Native app with small backend. First time using datomic. React Native coded in clojurescript."]]
               [:section.center
                (youtube-embed
                 "https://www.youtube.com/embed/1tTzRdFfq1Q?si=n_ubJhpBw4vrbvVf"
                 "Video of WantFu")]]})

     (let [img (partial project-img "zoweeq")]
       {:id "zoweeq"
        :title "ZoweeQ"
        :tags ["Rails" "Elasticsearch"]
        :thumb (img "post")
        :desc "Post ads via Instagram by including #zoweeq. Categorized and searchable."
        :page [:div#zoweeq
               [:section
                [:p "Killed when Instagram shut off API access. Hired a designer from 99designs. Worked on this as a side project while having a fulltime job.
Wrote a bunch of regular expressions parsed instagram body adding price, category, phone, and car details."]]
               [:section
                (youtube-embed
                 "https://www.youtube.com/embed/hVOAOrV2ZcE?si=HbKvLvQNUxGRps1T"
                 "Searching cars on ZoweeQ")]
               [:section.grid
                (img-with-caption
                 {:src (img "post")
                  :caption "Post on instagram, gets listed on site"})
                (img-with-caption
                 {:src (img "search")
                  :caption "Search page"})]]})

     (let [img (partial project-img "umbria")]
       {:id "umbria"
        :title "World of Umbria"
        :tags ["PHP" "CodeIgniter"]
        :thumb (img "home")
        :desc "Social fantasy steampunk pet growing, clicking, trading game."
        :page [:div#umbria
               [:section
                [:p "This is the first functional web app I've made back in 2012.
A major upgrade from a previous project and migrations were also written.
This had a community that paid real money for premium currency.
Remnants of it can be found on waybackmachine."]]
               [:section.grid
                (img-with-caption
                 {:src (img "home")
                  :caption "Home page, recovered with waybackmachine"})
                (img-with-caption
                 {:src (img "trade")
                  :caption "Showing trade"})
                (img-with-caption
                 {:src (img "erdumbria")
                  :caption "ERD diagram showing relationships"})]]})]))

(defn projects [req]
  (render
   [:div#projects.container
    [:h1 "Projects"]
    [:p "The following are notable projects I've created."]
    [:p "Most of these I have developed completely myself with regards to frontend, backend, and deployment."]
    (into [:div.projects]
          (map (fn [{:keys [id title link tags desc thumb]}]
                 (let [url (str "/projects/" id)]
                   [:div.project
                    [:div.info
                     [:h3 title]
                     (into [:div.tags]
                           (map #(vector :span %) tags))
                     [:p.desc desc]
                     [:div.links
                      [:a.btn {:href url} "More Details"]
                      (when link [:a.btn {:href link
                                          :target "_blank"} "Visit Site"])]]
                    [:a.preview
                     {:href url}
                     [:img
                      (merge
                       (image-dims thumb)
                       {:src thumb})]]]))
               all-projects))]
   {:nav :projects
    :title "@Naomarik - Projects"
    :req req
    :page-desc "List of web applications worked on."}))

(defn project [req project-id]
  (let [project (first (filter #(= (:id %) project-id) all-projects))
        {:keys [title tags desc page]} project]
    (render
     [:div#project.container.small
      [:h1 title]
      (into [:div.tags]
            (map #(vector :span %) tags))
      [:p.desc desc]
      page]
     {:page-desc (str "@Naomarik - " title " - " desc)
      :req req
      :title (str "@Naomarik - " title)})))
