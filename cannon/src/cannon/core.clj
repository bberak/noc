(ns cannon.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [cannon.vectors :as v]
            [cannon.forces :as f]))

(def max-speed 50)

(defn create-ball []
  (let [mass 4]
    {:location (v/create 0 (q/height))
     :velocity (v/create 32 -32)
     :mass mass
     :side (* mass 10)
     :surface-area mass
     :angle 0
     :angular-velocity 0
     :angular-acceleration 0}))

(defn setup []
  (q/frame-rate 60)
  {:balls []
   :forces {:gravity (v/create 0 8.7)
            :air {:density 0.1
                  :drag-coefficient 0.01}}})

(defn update-ball [ball forces]
  (let [mass (:mass ball)
        acceleration (v/divide (v/add (:gravity forces) (f/drag ball (:air forces))) mass)
        new-velocity (v/constrain-magnitude (v/add acceleration (:velocity ball)) max-speed)
        new-location (v/add new-velocity (:location ball))
        bounce-results (v/bounce new-location new-velocity)]
  (assoc ball 
           :location (:location bounce-results)
           :velocity (:velocity bounce-results))))

(defn append-ball [balls]
  (if (q/mouse-pressed?)
    (conj balls (create-ball))
    balls))

(defn update-state [{balls :balls forces :forces}]
  {:balls (append-ball (doall (map (fn [b] (update-ball b forces)) balls)))
   :forces forces})

(defn draw-state [{balls :balls}]
  (q/background 240)
  (q/fill 0 255 255)
  (q/rect-mode :center)
  (doseq [ball balls]
    (q/with-translation [(get-in ball [:location :x]) (get-in ball [:location :y])]       
      (q/rotate (:angle ball))
      (q/rect 0 0 (:side ball) (:side ball)))))

(defn -main [] 
  (q/defsketch cannon
  :title "Cannon"
  :size [1280 480]
  :setup setup
  :update update-state
  :draw draw-state
  :features [:keep-on-top]
  :middleware [m/fun-mode]))

