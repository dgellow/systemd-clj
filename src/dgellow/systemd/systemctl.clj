(ns dgellow.systemd.systemctl
  (:require [clojure.java.shell :refer [sh]]))

(defn systemctl [& args]
  (apply sh "systemctl" "--no-pager"
    (map (comp str name) (filter identity args))))

(defn- parse-list* [output]
  (->> output
     clojure.string/split-lines
     (map clojure.string/trim)
     (map #(clojure.string/replace % "  " " "))
     (map #(clojure.string/split % #" "))
     (map #(filter (fn [x] (not (clojure.string/blank? x))) %))))

(defn parse-list-units [output]
  (->> output
     parse-list*
     (drop 1)
     (drop-last 7)
     (map (fn [[unit load active sub & descr]]
            {:description (clojure.string/join " " descr)
             :unit unit
             :load load
             :active active
             :sub sub}))))

(defn list-units
  ([] (list-units nil))
  ([pattern & opts]
   (let [{:keys [exit out err] :as ret}
         (apply systemctl :list-units :--all pattern opts)]
     (if-not (= exit 0)
       ret
       (parse-list-units out)))))

(defn parse-list-unit-files [output]
  (->> output
     parse-list*
     (drop 1)
     (drop-last 2)
     (map (fn [[unit-file state]] {:unit-file unit-file :state state}))))

(defn list-unit-files
  ([] (list-unit-files nil))
  ([pattern & opts]
   (let [{:keys [exit out err] :as ret}
         (apply systemctl :list-unit-files pattern opts)]
     (if-not (= exit 0)
       ret
       (parse-list-unit-files out)))))

(defn parse-list-timers [output]
  (->> output
     parse-list*
     (drop 1)
     (drop-last 2)
     (map (fn [[next left last passed unit activates]]
            {:next next
             :left left
             :last last
             :passed passed
             :unit unit
             :activates activates}))))

(defn list-timers
  ([] (list-timers nil))
  ([pattern & opts]
   (let [{:keys [exit out err] :as ret}
         (apply systemctl :list-timers :--all pattern opts)]
     (if-not (= exit 0)
       ret
       (parse-list-timers out)))))
