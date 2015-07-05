using System;
using System.Collections.Generic;
using System.IO;
using System.Text;
using System.Threading.Tasks;
using Windows.Data.Json;
using Windows.Storage;
using Newtonsoft.Json;

namespace GovHack
{
    public class LocationsController
    {
        public List<Locations> locationsList;
        private bool alreadyRead = false;
        public async Task<List<Locations>> GetLocations()
        {
            if (alreadyRead)
            {
                return locationsList;
            }

            Uri uri = new Uri("ms-appx:///Assets/locations.json");
            StorageFile file = await Windows.Storage.StorageFile.GetFileFromApplicationUriAsync(uri);

            LocationClass locations;
            using (StreamReader r = new StreamReader(await file.OpenStreamForReadAsync()))
            {
                string json = r.ReadToEnd();
                locations = JsonConvert.DeserializeObject<LocationClass>(json);
                locationsList = locations.Locations;
            }
            alreadyRead = true;
            return locationsList;
        }




    
    }
}
