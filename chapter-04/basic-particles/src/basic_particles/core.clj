(ns basic-particles.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [basic-particles.particle]
            [basic-particles.vector]
            [basic-particles.protocols.particle-operations :refer :all])
  (:import [basic_particles.particle Particle]
           [basic_particles.vector Vector]))

(defn setup []
  (q/frame-rate 30)
  {:particle (Particle. 
               (Vector. 0 0)
               (Vector. 0 0)
               (Vector. 0 0)
               0)})

(defn update-state [{particle :particle}]
  {:particle (update-particle particle)})

(defn draw-state [state]
  (q/background 240)
  (q/fill 70 255 255))

(defn -main []
  (q/defsketch basic-particles
    :title "Basic Particles"
    :size [640 480]
    :setup setup
    :update update-state
    :draw draw-state
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
