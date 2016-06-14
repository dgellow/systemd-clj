(ns dgellow.systemd.systemctl
  (:require [clojure.java.shell :refer [sh]]))

(defn systemctl [& args]
  (apply sh "systemctl" "--no-pager" "--no-legend"
    (map (comp str name) (filter identity args))))

(defn- parse-list* [output]
   (->> output
      clojure.string/split-lines
      (map clojure.string/trim)
      (map #(clojure.string/replace % "  " " "))
      (map #(clojure.string/split % #" "))
      (map #(filter (fn [x] (not (clojure.string/blank? x))) %))))

(defmacro ^{:private true} deflistcommand* [name map-fn]
  `(defn ~name
     ([] (~name nil))
     ([pattern# & opts#]
      (let [ret# (apply systemctl (keyword '~name) pattern# opts#)
            {exit# :exit
             out# :out} ret#]
        (if-not (= exit# 0)
          ret#
          (->> out#
             parse-list*
             (map ~map-fn)))))))

(defmacro ^{:private true} deflistcommand [name keywords]
  `(deflistcommand* ~name
     (fn [[~@(map symbol keywords)]]
       ~(apply hash-map
          (flatten
           (map (fn [x] [(keyword x) x])
             keywords))))))

(deflistcommand* list-units
  (fn [[unit load active sub & descr]]
    {:description (clojure.string/join " " descr)
     :unit unit
     :load load
     :active active
     :sub sub}))

(deflistcommand list-sockets [listen unit activates])
(deflistcommand list-unit-files [unit-file state])
(deflistcommand list-timers [next left last passed unit activates])
