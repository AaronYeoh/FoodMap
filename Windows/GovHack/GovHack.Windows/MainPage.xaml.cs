using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices.WindowsRuntime;
using System.Threading.Tasks;
using Windows.Foundation;
using Windows.Foundation.Collections;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Controls.Primitives;
using Windows.UI.Xaml.Data;
using Windows.UI.Xaml.Input;
using Windows.UI.Xaml.Media;
using Windows.UI.Xaml.Navigation;
using Windows.Devices.Geolocation;
using Windows.Storage;
using Windows.UI.Xaml.Media.Imaging;
using Windows.UI.Xaml.Shapes;
using Bing.Maps;


// The Blank Page item template is documented at http://go.microsoft.com/fwlink/?LinkId=234238

namespace GovHack
{
    /// <summary>
    /// An empty page that can be used on its own or navigated to within a Frame.
    /// </summary>
    public sealed partial class MainPage : Page
    {
        LocationsController locationsController = new LocationsController();
        public MainPage()
        {
            this.InitializeComponent();

        }

        protected override async void OnNavigatedTo(NavigationEventArgs e)
        {
            Geolocator geolocator = new Geolocator();
            var geo = await geolocator.GetGeopositionAsync();

            MapControl.Center = new Location(geo.Coordinate.Latitude, geo.Coordinate.Longitude);
            MapControl.ZoomLevel = 15;

            Pushpin pushpin = new Pushpin();
            pushpin.Text = "1";

            MapLayer.SetPosition(pushpin, new Location(geo.Coordinate.Latitude, geo.Coordinate.Longitude));


            List<Locations> locations = await locationsController.GetLocations();

            AddPins(locations);
            AddPushpin(new Location(geo.Coordinate.Latitude, geo.Coordinate.Longitude), "Lemon","Hello", DataLayer, "W");
        }

        private void AddPins(List<Locations> locations)
        {
      
            foreach (var location in locations)
            {
                AddPushpin(location.Latitude, location.Longitude, location.Produce, location.Description);
            }
        
        }

        private void AddPushpin(double latitude, double longitude, string title, string description)
        {
            AddPushpin(new Location(latitude, longitude), title, description, DataLayer, "W");
        }


        private void CloseInfobox_Tapped(object sender, Windows.UI.Xaml.Input.TappedRoutedEventArgs e)
        {
            Infobox.Visibility = Visibility.Collapsed;
        }


        public async void AddPushpin(Location latlong, string title, string description, MapLayer layer, string pushpinText=null)
        {
            Debug.WriteLine(title);
            Image img = await StringToImage(title);

            var x = new Button
            {
                Width = 16, Height = 16, Content = img,
                Tag = new Metadata()
                {
                    Title = title,
                    Description = description
                }
            };
            x.Tapped += PinTapped;

            MapLayer.SetPosition(x, latlong);

            layer.Children.Add(x);
            //Pushpin p = new Pushpin()
            //{
                
            //};
           
            //MapLayer.SetPosition(p, latlong);

            //if (pushpinText != null)
            //{
            //    p.Text = pushpinText;
            //}

            //p.Tapped += PinTapped;

            //layer.Children.Add(p);
        }

        private async Task<Image> StringToImage(string title)
        {
            string fruit = title.ToLower();
            Debug.WriteLine(fruit);
            string str = String.Format("ms-appx:///Assets/Fruit/{0}.png", fruit);
            Debug.WriteLine(str);
            var uri = new Uri(str);
            StorageFile file;
            file = await StorageFile.GetFileFromApplicationUriAsync(uri);

            BitmapImage bitmap = new BitmapImage();
            Image img = new Image();
            if (file != null)
            {
                bitmap.UriSource = uri;
                img.Source = bitmap;
                return img;
            }
            else
            {
                return null;
            }
        }

        public class Metadata
        {
            public string Title { get; set; }
            public string Description { get; set; }
        }


        private void PinTapped(object sender, Windows.UI.Xaml.Input.TappedRoutedEventArgs e)
        {
            Pushpin p = sender as Pushpin;
            Metadata m = (Metadata)p.Tag;

            //Ensure there is content to be displayed before modifying the infobox control
            if (!String.IsNullOrEmpty(m.Title) || !String.IsNullOrEmpty(m.Description))
            {
                Infobox.DataContext = m;

                Infobox.Visibility = Visibility.Visible;

                MapLayer.SetPosition(Infobox, MapLayer.GetPosition(p));
            }
            else
            {
                Infobox.Visibility = Visibility.Collapsed;
            }
        }
    }
}
