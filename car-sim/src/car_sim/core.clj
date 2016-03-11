(ns car-sim.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [car-sim.vectors :as v]
            [car-sim.forces :as f]))

(def max-speed 5)
(def max-acceleration 1)

(defn setup []
  (q/frame-rate 60)
  (q/rect-mode :center)
  {:ground {:friction-coefficient 0.01}
   :car {:location (v/create 320 240)
         :velocity (v/create 0 0)
         :mass 5}})

;; You gotta learn to walk before you can run!
;;(defn get-acute-angle [heading]
;;  (let [heading-mod (mod heading 360)]
;;    (cond
;;      (< heading-mod 90) heading-mod
;;      (< heading-mod 180) (- heading-mod 90)
;;      (< heading-mod 270) (- heading-mod 180)
;;      :else (- heading-mod 270))));;

;;(defn get-quad-vector [heading]
;;  (let [heading-mod (mod heading 360)]
;;    (cond
;;      (< heading-mod 90) {:x 1 :y 1}
;;      (< heading-mod 180) {:x -1 :y 1}
;;      (< heading-mod 270) {:x -1 :y -1}
;;      :else {:x 1 :y -1})));;

;;(defn read-acceleration [{heading :heading}]
;;  (if (q/key-pressed?)
;;    (let [key (q/key-as-keyword)
;;          angle (get-acute-angle heading)
;;          quad (get-quad-vector heading)
;;          hyp (* max-acceleration 0.1)
;;          adj (* (q/cos (q/radians angle)) hyp)
;;          opp (q/sqrt (- (q/sq hyp) (q/sq adj)))
;;          acceleration-vector (v/cross (v/create adj opp) quad)]
;;      (println "heading: " heading)
;;      (println "angle: " angle)
;;      (println "quad: " quad)
;;      (println "hyp: " hyp)
;;      (println "adj: " adj)
;;      (println "opp: " opp)
;;      (println "accel: " acceleration-vector)
;;      (cond 
;;        (= key :up) (v/multiply acceleration-vector 1)
;;        (= key :down) (v/multiply acceleration-vector -1)
;;        :else (v/create 0 0)))
;;    (v/create 0 0)));;

;;(defn read-steering [{heading :heading}]
;;  (if (q/key-pressed?)
;;    (let [key (q/key-as-keyword)]
;;      (cond 
;;        (= key :left) (- heading 0.05)
;;        (= key :right) (+ heading 0.05)
;;        :else heading))
;;    heading))

(defn read-simple-acceleration [{heading :heading}]
  (if (q/key-pressed?)
    (let [key (q/key-as-keyword)]
      (cond 
        (= key :up) (v/create 0 -0.07)
        (= key :down) (v/create 0 0.07)
        (= key :left) (v/create -0.07 0)
        (= key :right) (v/create 0.07 0)
        :else (v/create 0 0)))
    (v/create 0 0)))

(defn update-state [{car :car ground :ground}]
  (let [acceleration (v/constrain-magnitude (v/divide (v/add (f/friction car ground) (read-simple-acceleration car)) (:mass car)) max-acceleration)
        new-velocity (v/constrain-magnitude (v/add (:velocity car) acceleration) max-speed)
        new-location (v/add (:location car) new-velocity)]
    {:ground ground
     :car (assoc car :velocity new-velocity :location new-location)}))

;; Implementing atan2 to solidify my knowledge
(defn heading [{x :x y :y}]
  (cond
    (or (zero? x) (zero? y)) 0
    ;;(and (> x 0) (zero? y))   (q/radians 0)
    ;;(and (> y 0) (zero? x))   (q/radians 90)
    ;;(and (< x 0) (zero? y))   (q/radians 180)
    ;;(and (< y 0) (zero? x))   (q/radians 270)
    (and (> x 0) (> y 0)) (let [opp y adj x] (q/atan (/ opp adj)))
    (and (< x 0) (> y 0)) (let [opp x adj y] (+ (q/abs (q/atan (/ opp adj))) (q/radians 90)))
    (and (< x 0) (< y 0)) (let [opp y adj x] (+ (q/abs (q/atan (/ opp adj))) (q/radians 180)))
    :else                 (let [opp x adj y] (+ (q/abs (q/atan (/ opp adj))) (q/radians 270)))))

(defn draw-state [{car :car}]
  (q/background 240)
  (q/fill 20 190 40)
  (let [x (get-in car [:location :x])
        y (get-in car [:location :y])
        velocity (:velocity car)
        angle (heading velocity)]
    (q/with-translation [x y]
      (q/with-rotation [angle]
        (q/rect 0 0 10 10)))))

(defn -main []
  (q/defsketch car-sim
    :title "Vrooom vroom"
    :size [640 480]
    :setup setup
    :update update-state
    :draw draw-state
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
