using System;
using System.Collections.Generic;
using System.Text;

namespace GovHack
{
    public class LocationClass
    {
        public List<Locations> Locations;
    }

    public class Locations
    {
        public string Produce { get; set; }
        public double Latitude { get; set; }
        public double Longitude { get; set; }
        public string Description { get; set; }
        public double AveragePricePerKilo { get; set; }
    }
}
