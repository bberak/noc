(ns basic-particles.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [basic-particles.particle]
            [basic-particles.vector]
            [basic-particles.protocols.particle-operations :refer :all])
  (:import [basic_particles.particle Particle]
           [basic_particles.vector Vector]))

(defn create-particle []
  (Particle. (Vector. (/ (q/width) 2) 100) (Vector. (q/random -1 1) (q/random -7 0)) 255 0))

(defn setup []
  (q/frame-rate 60)
  {:particles []
   :gravity (Vector. 0 0.275)
   :wind (Vector. -0.08 0.015)})

(defn update-state [{particles :particles gravity :gravity wind :wind}]
  {:particles (conj (filter is-alive? (map (fn [p] (update-particle p [gravity wind])) particles)) (create-particle))
   :gravity gravity
   :wind wind})

(defn draw-state [{particles :particles}]
  (q/background 240)
  (doseq [p particles] (render p)))

(defn -main []
  (q/defsketch basic-particles
    :title "Basic Particles"
    :size [640 480]
    :setup setup
    :update update-state
    :draw draw-state
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
