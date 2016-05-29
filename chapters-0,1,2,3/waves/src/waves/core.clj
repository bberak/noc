(ns waves.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [waves.vectors :as v]
            [waves.forces :as f]
            [waves.waves :as w]))

(defn create-wave [angular-velocity amplitude speed y-offset]
  {:angular-velocity angular-velocity
   :amplitude amplitude
   :speed speed
   :y-offset y-offset
   :angle 0
   :points []})

(defn setup []
  (q/frame-rate 60)
  {:waves [(create-wave 0.06 40 0.5 80) (create-wave 0.02 100 0.16667 250) (create-wave 0.01 20 0.25 400)]})

(defn update-wave [w]
  (let [amplitude (:amplitude w)
        angular-velocity (:angular-velocity w)
        speed (:speed w)
        start-angle (:angle w)]
    (assoc w 
      :angle (+ start-angle speed)
      :points (map (fn [x]
                     (let [a (+ start-angle (* x angular-velocity))] 
                        (v/create x (* amplitude (q/sin a))))) 
                    (range 0 (q/width))))))

(defn update-state [{waves :waves}]
  {:waves (doall (map (fn [w] (update-wave w)) waves))})

(defn draw-state [{waves :waves}]
  (q/background 255)
  (doseq [w waves]
    (q/with-translation [0 (:y-offset w)]
      (q/begin-shape)
      (doseq [pt (:points w)]
        (q/vertex (:x pt) (:y pt)))
      (q/end-shape))))

(defn -main []
  (q/defsketch waves
    :title "Waves"
    :size [640 480]
    :setup setup
    :update update-state
    :draw draw-state
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
