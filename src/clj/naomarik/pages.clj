(ns naomarik.pages
  (:require
   [naomarik.env :as env]
   [clojure.java.io :as io]
   [hiccup2.core :as hic]))

(def ^:private favicon "data:image/png;base64,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")

(defn- main-nav [page]
  (into [:nav
         (map (fn [[href title page*]]
                [:a {:href href
                     :class (when (= page page*) "active")
                     } title])
              [["/" (if (= @env/mode :dev) "/dev" "/") :home]
               ["/projects" "Projects" :projects]
               ["/verbiage" "Verbiage" :verbiage]
               ["/aboot" "Aboot" :aboot]])]))

(def ^:private css (slurp (io/file (str "build/css/style.css"))))

(defn render [page & [opts]]
  (let [{:keys [nav req page-desc title]} opts
        title (or title "@Naomarik")
        page-desc (or page-desc "@Naomarik's Portfolio Site")
        boosted? (= "true" (get-in req [:headers "hx-boosted"]))]
    (str
     "<!doctype html>"
     (hic/html
      [:html
       {:lang "en"}
       (-> [:head
            [:meta {:charset "UTF-8"}]
            [:link {:rel "preconnect"
                    :href "https://fonts.gstatic.com"
                    :crossorigin true}]
            [:link {:rel "preconnect"
                    :href "https://fonts.googleapis.com/"}]
            [:meta {:name "description" :content page-desc}]
            [:meta {:property "og:title" :content title}]
            [:meta {:property "og:description" :content page-desc}]
            [:meta {:name "viewport"
                    :content "width=device-width, initial-scale=1, maximum-scale=1, viewport-fit=cover"}]
            [:title title]]
           (cond-> (not boosted?)
             (into [[:style (hic/raw css)]
                    [:link {:href favicon :rel "icon" :type "image/x-icon"}]
                    [:link {:href (str "/css/fonts.css")
                            :rel "stylesheet"
                            :media "print"
                            :onload "this.rel='stylesheet'"
                            :type "text/css"}]])))
       [:body
        {:hx-boost "true"
         :hx-push-url "true"}
        (main-nav nav)
        [:div#page
         page]]
       (when-not boosted? [:script {:src "/js/index.min.js"}])
       (when (= @env/mode :dev)
         [:script {:src "https://livejs.com/live.js"}])]))))

(defn img-with-caption [{:keys [src caption height width]}]
  [:div.center
   [:figure
    {:style (cond-> {}
              height
              (assoc :height (str height "px"))
              width
              (assoc :width (str width "px")))}
    [:a {:href src
         :target "_blank"}
     [:img {:src src}]]
    [:figcaption caption]]])

(defn youtube-embed [url caption]
  [:div.center
   [:figure
    [:iframe {:width "560",
              :height "315"
              :src url,
              :title "YouTube video player"
              :frameborder "0",
              :allow "accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share",
              :referrerpolicy "strict-origin-when-cross-origin",
              :allowfullscreen true}]
    [:figcaption caption]]])

(defn aboot [req]
  (render
   [:div#aboot.container.small
    [:h1 "Aboot"]
    [:p "There's no doobt that you've landed on this aboot. If yar wondering who I am you've come to the right place."]
    [:section
     [:h2 "The Human"]
     [:p "Love delicious food, video games, cycling, archery, and playing with my kids. Californian, Muslim since 2007."]
     [:p "I'm a fairly private person publicly and don't do social media."]]
    [:section
     [:h2 "The Developer"]
     [:p "I've been doing fullstack development since 2011, working directly with CEO types before that title means anything to either of us. I've taken a lot of advice to heart from Paul Graham, reading his essays since 2014 trying to make this startup thing work for myself. I've experienced the highs and lows of startup life multiple times, survived on little until investment was secured and built things starting from the core problem outwards."]
     [:p "I believe in building something people want that enhances their experience in life.
Solving business problems in an impactful, robust way that lasts. Selling gimmicks and hype does not stand the test of time."]
     [:p "To more easily achieve the ever demanding need to mold software towards the requirements of reality, I have progressed from PHP to primarily using Clojure."]

     (img-with-caption
      {:src "/img/aboot/bike.jpg"
       :caption "Most likely wouldn't be programming today if I didn't lowside my GSXR1000"
       :width 370})]
    [:section
     [:h2 "The Site"]
     [:p "After exploring various static site generators, I decided to just make this from scratch with clojure.
It's using HTMX powered with http-kit and babashka, served behind nginx and Cloudflare."]
     [:a {:href "https://github.com/Naomarik/naomarik-site"} "source on Github"]]]
   {:nav :aboot
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
    :date "June 14, 2024"
    :slug "twitter-banking"
    :draft true
    :content
    [:div.content
     [:p "this is"]]}

   {:slug "why-clojure"
    :title "Clojure for the past 9 years and forseeable future"
    :date "June 14, 2024"
    :content
    [:div.content
     [:p "Professionally it started with PHP where I was building out custom Wordpress blogs from Photoshop files. I then (re)made a very complicated online collectible virtual fantasy pet platform."]
     [:p "Upon completion of that, I didn't want anything to do with PHP ever again. I learned Rails and TDD to help me solve the problems I had with the complexity refactoring a PHP project."]

     [:p "I jumped into web development just solving other people's needs without any formal background of software engineering. At some point when working on a side project in Rails, I realized I was a 'framework' programmer who was adept at doing things that had documentation and stitching libraries together, and so were my colleagues. Doing anything that strayed far away from the guard rails of what was provided to me wasn't very easy."]

     [:p "Early on in my career I attended a small hackathon event in Dubai for web developers. I destroyed the first two rounds, being the first one done and went beyond and above the requirements. Then they gave us some data in the form of csv and told us to visualize it. I froze up. This was unfamiliar territory, outside my ORM where I could do " [:code [:i "$object"] ".all.each"] " and iterate through it. I ended up spending the entire time allocated trying to hobble together a quick CSV to ORM library so I could work with the data in a manner that ORMs taught me to. I completely failed in doing that and ended up achieving nothing for that final round. It completely humbled my inflated ego from my first two wins."]

     [:p "At some point I read Paul Graham's "
      [:a {:href "https://www.paulgraham.com/avg.html"} "Beating the Averages"]
      " and felt I wanted to learn a LISP. Then around 2014, I read " [:a {:href "https://www.defmacro.org/ramblings/lisp.html"} "The Nature of Lisp"] " and remember my mind exploding from the enlightenment and learning a LISP was my only way forward."]

     [:p "I had another realization. The essence of what I've been doing all these years is just data, transforming data, and threading data through APIs. That's what both backend and frontend web development is at its most boiled down substance. Anything that gets you closer to that essense is going to be a better experience in getting things done."]

     [:p "Clojure puts data and data transformations first, and all the things that were magical to me using Rails and the ruby ecosystem are plain to see in Clojure. Using an ORM for half my programming career at that point and CLI tools to generate controllers, models, and migrations was like being great at ordering fast food but not knowing how to put ingredients together and cook your own meal."]

     [:p "Object oriented programming just adds layers of nonsense to data. Instead of just taking information (a data structure) and acting on it (a function) you have to name everything, instantiate objects and twirl your in your chair reasoning, wondering, about the behavior of some object that has inherited 50 classes interspersed with public and private fields."]

     [:p "While it's pretty cool that you can call code like " [:code "User.all"] ", coming from a background like I did means if there is no magical method on an object that did something, you're in hard mode territory."]

     [:p "I cannot believe the amount of libraries in the Ruby ecosystem that exist just to call APIs of random SaaS products just to instantiate objects with just JSON data that are mapped to fields so a junior developer could use the dot operator."]

     [:p
      [:a {:href "https://www.youtube.com/watch?v=ShEez0JkOFw"} "A talk given by Tim Ewald"]
      " explains the power of clojure by comparing it with woodworking. I've watched this twice, the second time when I started woodworking as a hobby. This talk completely matches my experience developing software. Just like in my early days, I was able to spit out models and pages at blazing speed using generators and libraries that did all the heavy lifting. But simply doing something with raw CSV data made my brain hurt."]

     [:p "Being proficient with molding data by hand makes you capable of making extremely bespoke things."]
     [:p "Since I've been using Clojure, I've transcended the limits of what I can achieve being contingent on whether or not a library or framework exists. This applies to any language, but Clojure's essence is strong data structures and core functions to manipulate them. You also have syntax irreducibility because it's a lisp and strong performance which allows me to run my web apps on cheap hardware. The ability to write code that compiles both on JVM and Javascript in the same file is superb for things like writing validation code once that works on both client side forms and backend."]

     [:p "This is why for at least business web applications, I will always default to Clojure when starting my own projects."]]}])

(defn post [req id]
  (let [item (first (filter #(= (:slug %) id) writing))
        {:keys [title date content]} item
        head (str "@Naomarik - " title)]
    (render
     [:div#post.container.small
      [:div.entry
       [:h2 title]
       [:span.date date] content]]
     {:page-desc head
      :req req
      :title head})))

(defn verbiage [req]
  (render
   [:div#verbiage.container.small
    [:h1 "Verbiage"]
    [:p "Where letters are spewed forth aboot random topics"]
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

(def ^:private *hits (atom 0))
(defn home-page [req]
  (render
   [:div#home.container
    [:h1 "Naomarik aka Omar Hughes "]

    [:div.grid
     [:div
      [:p "This is where you can find up to date information on my projects and writings."]
      [:p "Reach me by email with "
       [:strong
        "site at naomarik.com"]]]
     [:div
      (img-with-caption
       {:src
        "/img/home/ghaith.jpg"
        :caption "Baby Ghaith"
        :width 770})
      ]
     [:br]
     [:div.hits (str (swap! *hits inc) " hits since last update. Deployed "
                     @env/sha)]]]
   {:nav :home
    :req req
    :page-desc "@Naomarik - Home"}))

(def all-projects
  [{:id "motorsaif"
    :title "MotorSaif"
    :tags ["Clojure" "Datomic" "PWA" "LIVE"]
    :desc "Automotive services. Make a request, get responses in the form of organized live chats from businesses."
    :page [:div#motorsaif
           [:p "My current project. Solopreneur venture. Trying to fix the nightmare of getting a used car fixed."]
           [:p "See it in action "
            [:a {:href "https://motorsaif.com"
                 :target "_blank"} "MotorSaif"]

            (img-with-caption
             {:src "/img/projects/motorsaif/ms2.jpg"
              :caption "A successful request with receipt being sent as a picture in chat."
              :width 270})
            [:br]
            (img-with-caption
             {:src "/img/projects/motorsaif/ms1.jpg"
              :caption "Businesses respond to a request. Chat threads are grouped and organized."
              :width 270})]]}

   {:id "sayartii"
    :title "Sayartii"
    :tags ["Clojure" "Datomic" "Elasticsearch" "LIVE"]
    :desc "Car classified platform, serving ~1M unique users monthly."
    :page [:div#sayartii
           [:a {:href "https://sayartii.com"
                :target "_blank"} "Sayartii.com"]
           [:p "Created and maintaining entire site. Cool features include a dealership dashboard, infinite scroll, analytics on ads."]

           (youtube-embed "https://www.youtube.com/embed/sBzgPQ2a0bs?si=raT_5t2L3hBMABLz" "Video demonstrating site speed")
           [:br]
           (img-with-caption
            {:src "/img/projects/sayartii/analytics.png"
             :caption "Analytics each user gets. Notice the jump when ad is featured (F)"
             :width 270})
           [:br]
           (img-with-caption
            {:src "/img/projects/sayartii/backend.png"
             :caption "Backend showing ads posted per day"
             :width 370})]}

   {:id "aceplace"
    :title "Aceplace"
    :tags ["Clojure" "Datomic"]
    :desc "Booking platform for meetings and events."
    :page [:div#aceplace
           [:p "CTO of Aceplace for a time and made nearly this entire platform myself before I left after company ran out of funding. Coded entire initial MVP myself that led to our first bookings. Also hired and managed other devs."]
           [:p "Site was relaunched pivoting to booking only yachts."]]}

   {:id "booma"
    :title "Booma"
    :tags ["Clojure" "Postgres"]
    :desc "Live Chat SaaS with kanban dashboard."
    :page [:div#booma
           [:p "First clojure project nearly making my cofounder and I both young millionaires. (Un?)fortunately that didn't happen and I'm still grinding. It was used by Dubai Statistics's site for several months before running out of money and shutting down."]
           (img-with-caption
            {:src "/img/projects/booma/kanban.png"
             :caption "Kanban backend"
             :width 370})]}

   {:id "wantfu"
    :title "WantFu"
    :tags ["React Native" "Clojurescript" "Datomic"]
    :desc "Reverse classifieds. You want, you get."
    :page [:div#wantfu
           [:p "React Native app with small backend. First time using datomic. React Native coded in clojurescript."]
           (youtube-embed
            "https://www.youtube.com/embed/1tTzRdFfq1Q?si=n_ubJhpBw4vrbvVf"
            "Video of WantFu.")
           [:section]]}

   {:id "zoweeq"
    :title "ZoweeQ"
    :tags ["Rails" "Elasticsearch"]
    :desc "Post ads via Instagram by merely adding #zoweeq. Categorized and searchable."
    :page [:div#zoweeq
           [:p "Killed when Instagram shut off API access. Hired a designer from 99designs. Worked on this as a side project while having a fulltime job.
Wrote a bunch of regular expressions parsed instagram body adding metadata listing and categorize."]
           (youtube-embed
            "https://www.youtube.com/embed/hVOAOrV2ZcE?si=HbKvLvQNUxGRps1T"
            "Searching cars on ZoweeQ")]}

   {:id "umbria"
    :title "World of Umbria"
    :tags ["PHP" "CodeIgniter"]
    :desc "Social fantasy steampunk pet growing, clicking, trading game."
    :page [:div#umbria
           [:p "This is the first functional web app I've made back in 2012. A major upgrade from a previous project and migrations were also written. This had a community that paid real money for premium currency. Remnants of it can be found on waybackmachine."]
           (img-with-caption
            {:src "/img/projects/umbria/trade.jpg"
             :caption "Showing trade"
             :width 370})
           (img-with-caption
            {:src "/img/projects/umbria/erdumbria.jpg"
             :caption "ERD diagram showing relationships"
             :width 370})]}])

(defn projects [req]
  (render
   [:div#projects.container
    [:h1 "Projects"]
    [:p "The following are a list of notable projects I've worked on."]
    [:p "Most of these I have developed completely myself with regards to backend, frontend, and deployment."]
    (into [:div#entries]
          (map (fn [{:keys [id title tags desc]}]
                 (let [url (str "/projects/" id)]
                   [:div.entry
                    [:h3
                     [:a {:href url}
                      title]]
                    (into [:div.tags]
                          (map #(vector :span %) tags))
                    [:p.desc desc]
                    [:a {:href url} "Learn More"]]))
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
